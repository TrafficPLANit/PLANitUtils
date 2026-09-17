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
     * Constructor
     *
     * @param entityId entity the occurrence relates to, may be null
     * @param detail further context, may be null
     */
    protected Occurrence(final String entityId, final String detail) {
      this.entityId = entityId;
      this.detail = detail;
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
     */
    protected void increment(final String entityId, final String detail) {
      occurrences.increment();
      if (maxRetained == UNLIMITED_RETENTION || numRetained.getAndIncrement() < maxRetained) {
        retainedOccurrences.add(new Occurrence(entityId, detail));
      }
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
   * occurrences available for listing are bounded
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
    if (StringUtils.isNullOrBlank(templateId)) {
      throw new IllegalArgumentException("template id is required to collate an occurrence");
    }
    templatesById.computeIfAbsent(
        templateId, id -> new CollatedTemplate(id, maxRetainedOccurrences)).increment(entityId, detail);
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
   * Create the sample of retained entity ids shown alongside a template's count, listing at most the configured
   * sample size and marking that more exist where that is the case
   *
   * @param template to create the sample for
   * @return sample to append, empty when there is nothing to show
   */
  private String createSampleOfRetainedAsString(final CollatedTemplate template) {
    if (logSampleSizeOfRetained <= 0) {
      return "";
    }

    var sampleIds = template.getRetainedOccurrences().stream().filter(Occurrence::hasEntityId).map(
        Occurrence::getEntityId).limit(logSampleSizeOfRetained).collect(Collectors.toList());
    if (sampleIds.isEmpty()) {
      return "";
    }

    var more = template.getOccurrences() > sampleIds.size() ? ", ..." : "";
    return String.format("   e.g. %s%s", String.join(", ", sampleIds), more);
  }
}
