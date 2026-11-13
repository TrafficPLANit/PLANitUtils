package org.goplanit.utils;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.SimpleEdgeSegmentImpl;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.misc.ComparablePair;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.path.SimpleDirectedPath;
import org.goplanit.utils.path.SimpleDirectedPathImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathUtilTest {

  private static SimpleDirectedPathImpl path1;
  private static SimpleDirectedPathImpl path2;

  @BeforeAll
  public static void setup(){
    //todo: before we can do testing here we need simple implementations (non graph entities) in util repo for edge/directed edge/vertex and directed vertex as well
    //todo: too much work for now, but if time is found this would be helpful to do, for now park this test
    path1 = new SimpleDirectedPathImpl();
    path1.append(
            new SimpleEdgeSegmentImpl( (DirectedEdge) null, true), new SimpleEdgeSegmentImpl( (DirectedEdge) null, true) );
  }

  @AfterAll
  public static void afterAll(){
    IdGenerator.reset();
  }

  @Test
  public void OverlapTest(){
    //todo
  }

}
