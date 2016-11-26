import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleks on 7/27/16.
 */
public class AvlNodeTest {

  @Test
  public void testAFewValues() {

    int[] valuesToInsert = {7, 6, 2, 99, 17, 98, 3, 37, -5, 36, 4, 44, 52, 50, 12};
    testInsert(valuesToInsert);
  }

  @Test
  public void testCase1() {

    int[] valuesToInsert = {3, 2, 4, 5};
    AvlNode resultTree = testInsert(valuesToInsert);
    Assert.assertNotNull(resultTree);
  }

  @Test
  public void testMainUseCase() {
    int[] valuesToInsert = {14, 25, 21, 10, 23, 7, 26, 12, 30, 16};
    AvlNode resultTree = testInsert(valuesToInsert);
    Assert.assertEquals("Encountered count mismatch after inserting " + valuesToInsert,
        valuesToInsert.length, getTreeSize(resultTree));
    Assert.assertTrue("Encountered IOT issue after inserting " + valuesToInsert,
        assertValidIOT(resultTree));
    Assert.assertTrue("Tree is not balanced after inserting " + valuesToInsert,
        AvlNode.isNodeBalanced(resultTree));
  }

  private AvlNode testInsert(int[] valuesToInsert) {

    AvlNode tree = null;
    int expectedTreeSize = 1;

    for (int valueToInsert : valuesToInsert) {

      if (tree == null) {
        tree = new AvlNode(valueToInsert, null, null);
        continue;
      }
      tree = AvlNode.insert(tree, valueToInsert);
      expectedTreeSize++;
      Assert.assertEquals("Encountered count mismatch after inserting " + valueToInsert, expectedTreeSize,getTreeSize(tree));
      Assert.assertTrue("Encountered IOT issue after inserting " + valueToInsert, assertValidIOT(tree));
      Assert.assertTrue("Tree is not balanced after inserting " + valueToInsert, AvlNode.isNodeBalanced(tree));
    }

    return tree;
  }


  private int getTreeSize(AvlNode tree) {
    if (tree == null) {
      return 0;
    }

    return 1 + getTreeSize(tree.left) + getTreeSize(tree.right);
  }

  private boolean assertValidIOT(AvlNode tree) {
    if (tree == null) {
      return true;
    }

    Integer previousValue = null;
    final List<Integer> iotValues = getIOTValues(tree);
    for (Integer currentValue : iotValues) {

      if (previousValue == null) {
        previousValue = currentValue;
        continue;
      } else {

        if (previousValue >= currentValue) {
          return false;
        }
      }
      previousValue = currentValue;
    }

    return true;
  }

  private List<Integer> getIOTValues(AvlNode tree) {

    if (tree == null) {
      return new ArrayList<>();
    } else {
      List<Integer> leftList = getIOTValues(tree.left);
      List<Integer> rightList = getIOTValues(tree.right);

      List<Integer> returnList = new ArrayList<>();
      returnList.addAll(leftList);
      returnList.add(tree.val);
      returnList.addAll(rightList);

      return returnList;
    }
  }

  @Test
  public void testImbalancedSubtree() {

    /**
     * 5
     *  \
     *   6
     *    \
     *     7
     */
    AvlNode tree = AvlNode.insert(null, 5);
    Assert.assertEquals("Encountered count mismatch after inserting " + 5, 1, getTreeSize(tree));
    tree = tree.insert(tree, 6);
    Assert.assertEquals("Encountered count mismatch after inserting " + 6, 2, getTreeSize(tree));
    tree = tree.insert(tree, 7);
    Assert.assertEquals("Encountered count mismatch after inserting " + 7, 3, getTreeSize(tree));
    tree = tree.insert(tree, 8);
    Assert.assertEquals("Encountered count mismatch after inserting " + 8, 4, getTreeSize(tree));

    Assert.assertTrue(AvlNode.isNodeBalanced(tree));
  }
  
 @Test
  public void testLRClassification() {

    AvlNode tree = AvlNode.insert(null, 5);
    tree = tree.insert(tree, 6, false);
    tree = tree.insert(tree, 7, false);

    AvlNode imbalancedNode = getHighestImbalancedSubtree(tree);
    Assert.assertEquals(AvlNode.ImbalancedConfiguration.RR, AvlNode.getImbalanceType(imbalancedNode));

    tree = AvlNode.insert(null, 7);
    tree = tree.insert(tree, 6, false);
    tree = tree.insert(tree, 5, false);
    imbalancedNode = getHighestImbalancedSubtree(tree);
    Assert.assertEquals(AvlNode.ImbalancedConfiguration.LL, AvlNode.getImbalanceType(imbalancedNode));

    tree = AvlNode.insert(null, 7);
    tree = tree.insert(tree, 5, false);
    tree = tree.insert(tree, 6, false);
    imbalancedNode = getHighestImbalancedSubtree(tree);
    Assert.assertEquals(AvlNode.ImbalancedConfiguration.LR, AvlNode.getImbalanceType(imbalancedNode));

    tree = AvlNode.insert(null, 5);
    tree = tree.insert(tree, 7, false);
    tree = tree.insert(tree, 6, false);
    imbalancedNode = getHighestImbalancedSubtree(tree);
    Assert.assertEquals(AvlNode.ImbalancedConfiguration.RL, AvlNode.getImbalanceType(imbalancedNode));
  }

  @Test
  public void testLeftTreeRebalancing() {
    AvlNode tree = AvlNode.insert(null, 7);
    tree = tree.insert(tree, 6);
    tree = tree.insert(tree, 5);

    Assert.assertTrue(AvlNode.isNodeBalanced(tree));
  }

  @Test
  public void testRightTreeRebalancing() {
    AvlNode tree = AvlNode.insert(null, 5);
    tree = tree.insert(tree,6);
    tree = tree.insert(tree,7);

    Assert.assertTrue(AvlNode.isNodeBalanced(tree));
  }

  @Test
  public void testLeftRightTreeRebalancing() {
    AvlNode tree = AvlNode.insert(null, 7);
    tree = tree.insert(tree,5);
    tree = tree.insert(tree,6);

    Assert.assertTrue(AvlNode.isNodeBalanced(tree));
  }

  @Test
  public void testRightLeftTreeRebalancing() {
    AvlNode tree = AvlNode.insert(null, 5);
    tree = tree.insert(tree,7);
    tree = tree.insert(tree,6);

    Assert.assertTrue(AvlNode.isNodeBalanced(tree));
  }


  /**
   * This will get the highest-level imbalanced tree it encounters,
   * assuming that there are 0 or 1 such trees.
   *
   * @param root
   * @return
   */
  public static AvlNode getHighestImbalancedSubtree(AvlNode root) {

    if (root == null) {
      return null;
    }

    if (!AvlNode.isNodeBalanced(root)) {
      return root;
    }
    else {
      AvlNode leftImbalanced = getHighestImbalancedSubtree(root.left);
      AvlNode rightImbalanced = getHighestImbalancedSubtree(root.right);

      if (leftImbalanced != null) {
        return leftImbalanced;
      } else if (rightImbalanced != null) {
        return rightImbalanced;
      } else {
        return null;
      }
    }
  }
}
