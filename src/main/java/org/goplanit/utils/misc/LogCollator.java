package org.goplanit.utils.misc;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Collects repeatedly occurring log worthy events by template and reports each template once rather than once per
 * occurrence.
 * <p>
 * A parser that meets the same condition for thousands of entities produces a log in which the conditions worth
 * reading cannot be told apart from the ones that merely repeat. Collating by template inverts that: a condition
 * appears once with its total, accompanied by the first few entity ids that triggered it, which is enough to begin an
 * investigation without displacing everything else in the log.
 * </p>
 * <p>
 * Every occurrence is retained by default, so a collapsed log entry can still be backed by a complete per entity
 * listing elsewhere, for instance when persisted to disk. Where a template is expected to fire for a very large number
 * of entities, retention can be capped at construction, in which case the total remains exact while only the retained
 * prefix is available for listing.
 * </p>
 * <p>
 * Safe for concurrent use: occurrences may be recorded from several threads at once.
 * </p>
 *
 * @author markr
 */
public class LogCollator {

  /** default number of retained entity ids listed per template when logging a summary */
  public static final int DEFAULT_LOG_SAMPLE_SIZE_OF_RETAINED = 5;

  /** retention limit indicating that every occurrence is retained */
  public static final int UNLIMITED_RETENTION = -1;

  /** retention limit indicating that occurrences are counted but none are retained */
  public static final int NO_RETENTION = 0;

  /**
   * A single recorded occurrence, identifying the entity that triggered it and any further context the call site
   * could supply
   */
  public static class Occurrence {

    /** entity the occurrence relates to, may be null when the condition is not entity specific */
    private final String entityId;

    /** further context, may be null */
    private final String detail;

    /**
     * Further context in expanded form, for consumers with more room than a log line, may be null in which case the
     * regular detail stands in
     */
    private final String expandedDetail;

    /**
     * Constructor
     *
     * @param entityId entity the occurrence relates to, may be null
     * @param detail further context, may be null
     * @param expandedDetail further context in expanded form, null to use the regular detail
     */
    protected Occurrence(final String entityId, final String detail, final String expandedDetail) {
      this.entityId = entityId;
      this.detail = detail;
      this.expandedDetail = expandedDetail != null ? expandedDetail : detail;
    }

    /**
     * Collect the entity the occurrence relates to
     *
     * @return entity id, may be null
     */
    public String getEntityId() {
      return entityId;
    }

    /**
     * Verify if an entity is identified
     *
     * @return true when an entity id is present, false otherwise
     */
    public boolean hasEntityId() {
      return !StringUtils.isNullOrBlank(entityId);
    }

    /**
     * Collect the further context supplied by the call site
     *
     * @return detail, may be null
     */
    public String getDetail() {
      return detail;
    }

    /**
     * Verify if further context is available
     *
     * @return true when detail is present, false otherwise
     */
    public boolean hasDetail() {
      return !StringUtils.isNullOrBlank(detail);
    }

    /**
     * Collect the further context in expanded form, falling back on the regular detail when the call site supplied only
     * the one
     *
     * @return expanded detail, may be null
     */
    public String getExpandedDetail() {
      return expandedDetail;
    }

    /**
     * Verify if further context in expanded form is available
     *
     * @return true when expanded detail is present, false otherwise
     */
    public boolean hasExpandedDetail() {
      return !StringUtils.isNullOrBlank(expandedDetail);
    }
  }

  /**
   * All occurrences recorded under a single template, i.e. the exact total paired with the occurrences retained for
   * listing
   */
  public static class CollatedTemplate {

    /** short readable label identifying the condition, used as the key and in the log */
    private final String templateId;

    /** exact number of occurrences, irrespective of how many are retained */
    private final LongAdder occurrences = new LongAdder();

    /** the retained occurrences, in the order they were recorded */
    private final Queue<Occurrence> retainedOccurrences = new ConcurrentLinkedQueue<>();

    /** tracked separately since determining the size of a concurrent queue is not a constant time operation */
    private final AtomicInteger numRetained = new AtomicInteger(0);

    /** upper bound on retained occurrences, or {@link #UNLIMITED_RETENTION} */
    private final int maxRetained;

    /**
     * Constructor
     *
     * @param templateId short readable label identifying the condition
     * @param maxRetained upper bound on retained occurrences, or {@link #UNLIMITED_RETENTION}
     */
    protected CollatedTemplate(final String templateId, final int maxRetained) {
      this.templateId = templateId;
      this.maxRetained = maxRetained;
    }

    /**
     * Record an occurrence, retaining it when the retention limit allows
     *
     * @param entityId entity the occurrence relates to, may be null
     * @param detail further context, may be null
     * @param expandedDetail further context in expanded form, null to use the regular detail
     */
    protected void increment(final String entityId, final String detail, final String expandedDetail) {
      occurrences.increment();
      if (maxRetained == NO_RETENTION) {
        /* counting only, so do not pay for the retention bookkeeping on an occurrence that is never kept */
        return;
      }
      if (maxRetained == UNLIMITED_RETENTION || numRetained.getAndIncrement() < maxRetained) {
        retainedOccurrences.add(new Occurrence(entityId, detail, expandedDetail));
      }
    }

    /**
     * Record an occurrence without retaining it, keeping the total exact while listing nothing
     */
    protected void incrementCountOnly() {
      occurrences.increment();
    }

    /**
     * Verify whether a further occurrence would be retained
     *
     * @return true when it would, false otherwise
     */
    protected boolean isRetaining() {
      return maxRetained == UNLIMITED_RETENTION || numRetained.get() < maxRetained;
    }

    /**
     * Absorb another template's occurrences, keeping the exact total while retaining as many of the other's retained
     * occurrences as this template's own limit still permits
     *
     * @param other to absorb
     */
    protected void absorb(final CollatedTemplate other) {
      long otherOccurrences = other.getOccurrences();
      if (maxRetained != NO_RETENTION) {
        for (var occurrence : other.retainedOccurrences) {
          if (maxRetained != UNLIMITED_RETENTION && numRetained.getAndIncrement() >= maxRetained) {
            break;
          }
          retainedOccurrences.add(occurrence);
        }
      }
      /* added last so the total is never briefly lower than what has already been retained against it */
      occurrences.add(otherOccurrences);
    }

    /**
     * Collect the label identifying the condition
     *
     * @return template id
     */
    public String getTemplateId() {
      return templateId;
    }

    /**
     * Collect the exact number of occurrences recorded, which exceeds the number retained when a retention limit
     * is in force
     *
     * @return number of occurrences
     */
    public long getOccurrences() {
      return occurrences.sum();
    }

    /**
     * Collect the retained occurrences in the order they were recorded
     *
     * @return retained occurrences
     */
    public List<Occurrence> getRetainedOccurrences() {
      return List.copyOf(retainedOccurrences);
    }

    /**
     * Verify whether occurrences were recorded but not retained due to the retention limit
     *
     * @return true when the total exceeds the number retained, false otherwise
     */
    public boolean hasUnretainedOccurrences() {
      return getOccurrences() > retainedOccurrences.size();
    }
  }

  /** the templates recorded so far, keyed by their id */
  private final Map<String, CollatedTemplate> templatesById = new ConcurrentHashMap<>();

  /** upper bound on retained occurrences per template, or {@link #UNLIMITED_RETENTION} */
  private final int maxRetainedOccurrences;

  /** number of retained entity ids listed per template when logging a summary */
  private int logSampleSizeOfRetained = DEFAULT_LOG_SAMPLE_SIZE_OF_RETAINED;

  /**
   * Constructor
   *
   * @param maxRetainedOccurrences upper bound on retained occurrences per template, or {@link #UNLIMITED_RETENTION}
   */
  protected LogCollator(final int maxRetainedOccurrences) {
    this.maxRetainedOccurrences = maxRetainedOccurrences;
  }

  /**
   * Create a collator retaining every occurrence
   *
   * @return created collator
   */
  public static LogCollator create() {
    return new LogCollator(UNLIMITED_RETENTION);
  }

  /**
   * Create a collator retaining at most the given number of occurrences per template. Totals remain exact; only the
   * occurrences available for listing are bounded. Pass {@link #NO_RETENTION} to count occurrences without retaining
   * any, for templates expected to fire for a very large number of entities
   *
   * @param maxRetainedOccurrences upper bound on retained occurrences per template
   * @return created collator
   */
  public static LogCollator createWithRetentionLimit(final int maxRetainedOccurrences) {
    return new LogCollator(maxRetainedOccurrences);
  }

  /**
   * Record an occurrence of a condition that is not specific to an entity
   *
   * @param templateId short readable label identifying the condition
   */
  public void increment(final String templateId) {
    increment(templateId, null, null);
  }

  /**
   * Record an occurrence of a condition for a given entity
   *
   * @param templateId short readable label identifying the condition
   * @param entityId entity the occurrence relates to, may be null
   */
  public void increment(final String templateId, final String entityId) {
    increment(templateId, entityId, null);
  }

  /**
   * Record an occurrence of a condition for a given entity, with further context retained for later listing but not
   * shown in the log summary
   *
   * @param templateId short readable label identifying the condition
   * @param entityId entity the occurrence relates to, may be null
   * @param detail further context, may be null
   */
  public void increment(final String templateId, final String entityId, final String detail) {
    increment(templateId, entityId, detail, null);
  }

  /**
   * Record an occurrence of a condition for a given entity, with further context in both a form suited to a log line
   * and an expanded form for consumers that list occurrences in full
   *
   * @param templateId short readable label identifying the condition
   * @param entityId entity the occurrence relates to, may be null
   * @param detail further context, may be null
   * @param expandedDetail further context in expanded form, null to use the regular detail
   */
  public void increment(
      final String templateId, final String entityId, final String detail, final String expandedDetail) {
    if (StringUtils.isNullOrBlank(templateId)) {
      throw new IllegalArgumentException("template id is required to collate an occurrence");
    }
    templatesById.computeIfAbsent(
        templateId, id -> new CollatedTemplate(id, maxRetainedOccurrences)).increment(
        entityId, detail, expandedDetail);
  }

  /**
   * Record an occurrence of a condition that is counted but never listed, i.e. one whose individual cases carry no
   * information worth keeping. Retaining occurrences of such a condition costs memory and displaces the retained
   * occurrences of conditions that are worth reading
   *
   * @param templateId short readable label identifying the condition
   */
  public void incrementCountOnly(final String templateId) {
    if (StringUtils.isNullOrBlank(templateId)) {
      throw new IllegalArgumentException("template id is required to collate an occurrence");
    }
    templatesById.computeIfAbsent(
        templateId, id -> new CollatedTemplate(id, maxRetainedOccurrences)).incrementCountOnly();
  }

  /**
   * Verify whether a further occurrence of a template would still be retained, so that a caller can avoid composing
   * context that would be discarded on arrival.
   * <p>
   * Retention of a template only ever runs out and never reopens, so an occurrence answered with false would not have
   * been retained by the time it was recorded either
   * </p>
   *
   * @param templateId to verify for
   * @return true when a further occurrence would be retained, false otherwise
   */
  public boolean isRetaining(final String templateId) {
    if (maxRetainedOccurrences == NO_RETENTION) {
      return false;
    }
    if (maxRetainedOccurrences == UNLIMITED_RETENTION) {
      return true;
    }
    var template = templatesById.get(templateId);
    return template == null || template.isRetaining();
  }

  /**
   * Verify whether a further occurrence of a template would still fall within the sample the log summary shows, so that
   * a caller can avoid composing context for an occurrence that is counted but never printed.
   * <p>
   * The sample only ever fills up, so an occurrence answered with false would not have been shown by the time it was
   * recorded either
   * </p>
   *
   * @param templateId to verify for
   * @return true when a further occurrence would be shown, false otherwise
   */
  public boolean isWithinLogSample(final String templateId) {
    if (logSampleSizeOfRetained <= 0) {
      return false;
    }
    var template = templatesById.get(templateId);
    return template == null || template.getOccurrences() < logSampleSizeOfRetained;
  }

  /**
   * Absorb everything another collator recorded, leaving the other untouched.
   * <p>
   * Totals are exact afterwards, whereas the retained occurrences are those of this collator followed by as many of
   * the other's as this collator's own retention limit still permits, so a merge never retains more than a single
   * collator would have
   * </p>
   *
   * @param other to absorb, ignored when null
   */
  public void merge(final LogCollator other) {
    if (other == null) {
      return;
    }
    other.templatesById.forEach((templateId, otherTemplate) -> templatesById.computeIfAbsent(
        templateId, id -> new CollatedTemplate(id, maxRetainedOccurrences)).absorb(otherTemplate));
  }

  /**
   * Verify whether anything was recorded at all
   *
   * @return true when no occurrence was recorded, false otherwise
   */
  public boolean isEmpty() {
    return templatesById.isEmpty();
  }

  /**
   * Collect the number of occurrences recorded for a given template
   *
   * @param templateId to collect for
   * @return number of occurrences, zero when the template was never recorded
   */
  public long getOccurrences(final String templateId) {
    var template = templatesById.get(templateId);
    return template != null ? template.getOccurrences() : 0;
  }

  /**
   * Collect the number of occurrences recorded across all templates
   *
   * @return total number of occurrences
   */
  public long getTotalOccurrences() {
    return templatesById.values().stream().mapToLong(CollatedTemplate::getOccurrences).sum();
  }

  /**
   * Collect the ids of all templates recorded so far
   *
   * @return template ids
   */
  public Collection<String> getTemplateIds() {
    return Set.copyOf(templatesById.keySet());
  }

  /**
   * Collect a template by its id
   *
   * @param templateId to collect
   * @return the template, null when never recorded
   */
  public CollatedTemplate getTemplate(final String templateId) {
    return templatesById.get(templateId);
  }

  /**
   * Collect all templates ordered by number of occurrences descending, so the most prolific condition comes first,
   * with the template id as a tie break to keep the ordering stable between runs
   *
   * @return ordered templates
   */
  public List<CollatedTemplate> getTemplatesByOccurrencesDescending() {
    return templatesById.values().stream().sorted(
        Comparator.comparingLong(CollatedTemplate::getOccurrences).reversed().thenComparing(
            CollatedTemplate::getTemplateId)).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Collect the number of retained entity ids listed per template when logging a summary
   *
   * @return log sample size of retained occurrences
   */
  public int getLogSampleSizeOfRetained() {
    return logSampleSizeOfRetained;
  }

  /**
   * Set the number of retained entity ids listed per template when logging a summary, so a condition under
   * investigation can be widened without affecting what is recorded
   *
   * @param logSampleSizeOfRetained to use, a non-positive value suppresses the listing
   */
  public void setLogSampleSizeOfRetained(final int logSampleSizeOfRetained) {
    this.logSampleSizeOfRetained = logSampleSizeOfRetained;
  }

  /**
   * Discard everything recorded so far
   */
  public void reset() {
    templatesById.clear();
  }

  /**
   * Log one line per template, most prolific first, each stating the number of occurrences followed by a sample of
   * the retained entity ids that triggered it
   *
   * @param logger to log to
   * @param sectionTitle logged ahead of the templates, verbatim, so the caller controls how the section is marked
   */
  public void logSummary(final Logger logger, final String sectionTitle) {
    logSummary(logger, sectionTitle, 0);
  }

  /**
   * Log one line per template, most prolific first, each stating the number of occurrences followed by a sample of
   * the retained entity ids that triggered it. A section that recorded nothing is reported as such rather than
   * omitted, so a silent zero is not mistaken for a section that was never reached
   *
   * @param logger to log to
   * @param sectionTitle logged ahead of the templates, verbatim, so the caller controls how the section is marked
   * @param denominator total the occurrences are a share of, when positive each count is accompanied by that share
   */
  public void logSummary(final Logger logger, final String sectionTitle, final long denominator) {
    logger.info(sectionTitle);
    if (isEmpty()) {
      logger.info(LoggingUtils.settingsEntry("none recorded", 1));
      return;
    }

    for (var template : getTemplatesByOccurrencesDescending()) {
      logger.info(LoggingUtils.settingsValue(
          template.getTemplateId(),
          LoggingUtils.countWithPercentage(template.getOccurrences(), denominator) +
              createSampleOfRetainedAsString(template),
          1));
    }
  }

  /**
   * Create the sample of retained entities shown alongside a template's count, listing at most the configured sample
   * size and marking that more exist where that is the case. An entity is accompanied by the context the call site
   * supplied, so that the line states what kind of case this is rather than only how many there were
   *
   * @param template to create the sample for
   * @return sample to append, empty when there is nothing to show
   */
  private String createSampleOfRetainedAsString(final CollatedTemplate template) {
    if (logSampleSizeOfRetained <= 0) {
      return "";
    }

    var samples = template.getRetainedOccurrences().stream().filter(Occurrence::hasEntityId).limit(
        logSampleSizeOfRetained).map(
        occurrence -> occurrence.hasDetail()
            ? String.format("%s (%s)", occurrence.getEntityId(), occurrence.getDetail())
            : occurrence.getEntityId()).collect(Collectors.toList());
    if (samples.isEmpty()) {
      return "";
    }

    var more = template.getOccurrences() > samples.size() ? ", ..." : "";
    return String.format("   e.g. %s%s", String.join(", ", samples), more);
  }
}
