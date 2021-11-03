package org.goplanit.utils.cumulatives;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;

/**
 * Represents a piecewise linear cumulative curve
 * 
 * @author markr
 *
 */
public class PiecewiseLinearCumulativeCurve {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(PiecewiseLinearCumulativeCurve.class.getCanonicalName());

  /** list of consecutive cumulative points */
  final LinkedList<CumulativePoint> cumulatives = new LinkedList<CumulativePoint>();

  /**
   * Constructor
   */
  public PiecewiseLinearCumulativeCurve() {

  }

  /**
   * append a new cumulative point to the curve which must be later or equal than the current latest
   * point
   * given the passed in timeInSecondsEpsilon
   * 
   * @param cumulativePoint to add
   * @param timeInSecondsEpsilon epsilon
   * @throws PlanItException thrown if error
   */
  public void add(CumulativePoint cumulativePoint, double timeInSecondsEpsilon) throws PlanItException {
    CumulativePoint currentLast = cumulatives.getLast();
    if (!cumulativePoint.isLater(currentLast, timeInSecondsEpsilon)) {
      String errorMessage =
          "new cumulative point does not occur later than last available cumulative point in cumulative curve, this is not allowed";
      LOGGER.severe(errorMessage);
      throw new PlanItException(errorMessage);
    }
    cumulatives.add(cumulativePoint);
  }

  /**
   * the total surface under this curve
   * 
   * @return total surface under this curve
   */
  public double computeSurfaceUnderCurve() {
    double totalSurfaceUnderCurve = 0;
    CumulativePoint previousPoint = null;
    for (CumulativePoint currentPoint : cumulatives) {
      totalSurfaceUnderCurve += computeSurfaceUnderSegment(previousPoint, currentPoint);
      previousPoint = currentPoint;
    }
    return totalSurfaceUnderCurve;
  }

  /**
   * compute the surface between two consecutive points bounded by zero (cumulatives) and the
   * point's time
   * stamps. In case the previous point is null, 0 is returned
   * 
   * @param previousPoint can be null (0 returned in that case)
   * @param currentPoint the current point
   * @return surface under the segment
   */
  private double computeSurfaceUnderSegment(CumulativePoint previousPoint, CumulativePoint currentPoint) {
    double surfaceUnderSegment = 0;
    if (previousPoint != null) {
      // rectangular surface + triangle surface = total segment surface
      double width = previousPoint.getAbsTimeDifferenceWith(currentPoint);
      // 1) rectangular surface
      double rectangularSurface = previousPoint.getCumulativeCount() * width;
      // double triangular surface
      double triangularSurface = previousPoint.getAbsCumulativeDifferenceWith(currentPoint) * width * 0.5;
      // total
      surfaceUnderSegment = rectangularSurface + triangularSurface;
    }
    return surfaceUnderSegment;
  }
}
