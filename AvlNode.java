class AvlNode {

  int val;         //Value
  int ht;          //Height
  AvlNode left;    //Left child
  AvlNode right;   //Right child

  public static int NULL_NODE_HEIGHT = -1;

  /**
   * Enum describing how a particular tree may be misbalanced
   */
  enum ImbalancedConfiguration {
    LR,
    RL,
    LL,
    RR
  }

  /**
   * Convention is that for a null node, height is -1. For an existing node, height is 0
   * @param val the value
   * @param left the left child
   * @param right the right child
   */
  public AvlNode(int val, AvlNode left, AvlNode right) {
    this.val = val;
    this.left = left;
    this.right = right;

    int leftHeight = left == null ? NULL_NODE_HEIGHT : left.ht;
    int rightHeight = right == null ? NULL_NODE_HEIGHT : right.ht;

    this.ht = 1 + Math.max(leftHeight, rightHeight);
  }

  /**
   * Insert a node into parent 'root' with value 'val'
   * @param root The parent node of what is to be inserted
   * @param val The new value to insert
   * @return the inserted node
   */
  static AvlNode insert(AvlNode root, int val) {
    return insert(root, val, true);
  }

  /**
   *
   * @param root root of the current tree
   * @param val the value to be inserted
   * @return the inserted node
   */
  public static AvlNode insert(AvlNode root, int val, boolean rebalance) {

    AvlNode newRoot;

    if (root == null) {
      // This is a new tree
      return new AvlNode(val, null, null);
    }

    //Insert without regard for balance, but with regard for ordering
    basicInsert(root, val);

    if (rebalance) {
      newRoot = rebalance(root);
      if (!isNodeBalanced(newRoot)) {
        throw new RuntimeException("Could not balance tree!?");
      }
    } else {
      newRoot = root;
    }

    return newRoot;
  }

  /**
   * Rebalance any issues with the tree, and return the new root.
   * @param root The new root
   * @return
   */
  private static AvlNode rebalance(AvlNode root) {

    if (!isNodeBalanced(root)) {
      return performPointRebalance(root);
    }
    return root;
  }

  /**
   * Return the newly-inserted node
   * Assume that all values are unique for a deterministic sorted order
   * Assume that when a basic insert is performed, that the tree is balanced
   * @return the node being inserted
   */
  static AvlNode basicInsert(AvlNode root, int val) {
    AvlNode returnNode = null;

    if (val > root.val) {
      if (root.right == null) {
        root.right = new AvlNode(val, null, null);
      } else {
        // Insert into the right tree and update height of current tree
        returnNode = basicInsert(root.right, val);
      }
      // Update height of current node
      if (root.left != null) {
        root.ht = (1 + Math.max(root.left.ht, root.right.ht));
      } else {
        root.ht = 1 + root.right.ht;
      }
    } else {
      if (root.left == null) {
        root.left = new AvlNode(val, null, null);
      } else {
        returnNode = basicInsert(root.left, val);
      }
      if (root.right != null) {
        root.ht = (1 + Math.max(root.left.ht, root.right.ht));
      } else {
        root.ht = 1 + root.left.ht;
      }
    }
    return returnNode;
  }

  /**
   * Return if a subtree roote at root is balanced
   * @param root root of subtree beinge examined
   * @return boolean
   */
  public static boolean isNodeBalanced(AvlNode root) {
    if (root == null) {
      return true;
    }

    int leftHeight = root.left == null ? NULL_NODE_HEIGHT : root.left.ht;
    int rightHeight = root.right == null ? NULL_NODE_HEIGHT : root.right.ht;

    // Let's assume for now that the height reading is accurate
    if (Math.abs(leftHeight - rightHeight) > 1) {
      return false;
    }
    return true;
  }

  public void recalcHeight() {
    int leftHeight = this.left == null ? NULL_NODE_HEIGHT : this.left.ht;
    int rightHeight = this.right == null ? NULL_NODE_HEIGHT : this.right.ht;

    this.ht = Math.max(leftHeight, rightHeight) + 1;
  }

  /**
   * Perform a rebalance on a given subtree (node)
   * @param node The subtree that needs to be rebalanced
   * @return the new root
   */
  public static AvlNode performPointRebalance(AvlNode node) {
    return AvlNode.performPointRebalance(node, AvlNode.getImbalanceType(node));
  }

  /**
   * Fix an imbalanced subtree
   * @param node The root of the subtree to be rebalanced
   * @param imbalanceType the configuration that needs to be fixed
   * @return the new root
   */
  public static AvlNode performPointRebalance(AvlNode node, ImbalancedConfiguration imbalanceType) {
    if (imbalanceType == null) {
      return node;
    }

    if (imbalanceType == ImbalancedConfiguration.LL) {
      return performLLRebalance(node);

    } else if (imbalanceType == ImbalancedConfiguration.LR) {

      // turn it into a LL situation.
      AvlNode leftNode = node.left;
      AvlNode leftRightNode = node.left.right;
      AvlNode lrNodeLeftSubtree = node.left.right.left;

      node.left = leftRightNode;
      leftRightNode.left = leftNode;
      leftNode.right = lrNodeLeftSubtree;

      //update heights
      leftNode.recalcHeight();
      leftRightNode.recalcHeight();

      // then do the LL fix
      return performLLRebalance(node);

    } else if (imbalanceType == ImbalancedConfiguration.RL) {
      // turn it into a RR situation.
      AvlNode rightNode = node.right;
      AvlNode rightLeftNode = node.right.left;
      AvlNode rlNodeLeftSubtree = node.right.left.right;

      node.right = rightLeftNode;
      rightLeftNode.right = rightNode;
      rightNode.left = rlNodeLeftSubtree;

      //update heights
      rightNode.recalcHeight();
      rightLeftNode.recalcHeight();

      // then do the RR fix
      return performRRRebalance(node);
    } else {
      // RR config
      return performRRRebalance(node);
    }
  }

  /**
   * Will perform a rebalance on a tree that is rebalanced in an LL configuration
   * @param node the node of the tree to be rebalanced
   * @return the root of the rebalanced tree
   */
  public static AvlNode performLLRebalance(AvlNode node) {
    // This becomes the new head
    AvlNode leftNode = node.left;
    AvlNode leftNodeRightSubtree = leftNode.right;

    leftNode.right = node;
    node.left = leftNodeRightSubtree;

    //Update heights. Order matters.
    node.recalcHeight();
    leftNode.recalcHeight();

    return leftNode;
  }

  /**
   * Will perform a rebalance on a tree that is rebalanced in an RR configuration
   * @param node the root of the tree to be rebalanced
   * @return the root of the rebalanced tree
   */
  public static AvlNode performRRRebalance(AvlNode node) {

    if (node.right == null) {
      throw new IllegalArgumentException("Cannot perform RR rebalance when node.right is null");
    }
    if (node.right.right == null) {
      throw new IllegalArgumentException("Cannot perform RR rebalance when node.right.right is null");
    }

    AvlNode rightNode = node.right;
    AvlNode rightNodeLeftSubtree = rightNode.left;

    rightNode.left = node;
    node.right = rightNodeLeftSubtree;

    //Update heights. Order matters.
    node.recalcHeight();
    rightNode.recalcHeight();

    return rightNode;
  }

  public int getLeftHeight() {
    if (this.left == null) {
      return NULL_NODE_HEIGHT;
    } else {
      return this.left.ht;
    }
  }

  public int getRightHeight() {
    if (this.right == null) {
      return NULL_NODE_HEIGHT;
    } else {
      return this.right.ht;
    }
  }

  /**
   * Will classify the type of imbalance of a given node, so appropriate
   * action can be taken
   * @param node
   * @return
   */
  public static AvlNode.ImbalancedConfiguration getImbalanceType(AvlNode node) {

    if(node.getLeftHeight() > node.getRightHeight()) {
      //LL or LR case
      AvlNode leftNode = node.left;
      if (leftNode.getLeftHeight() > leftNode.getRightHeight()) {
        return ImbalancedConfiguration.LL;
      } else {
        return ImbalancedConfiguration.LR;
      }
    } else {
      AvlNode rightNode = node.right;
      if (rightNode.getRightHeight() > rightNode.getLeftHeight()) {
        return ImbalancedConfiguration.RR;
      } else {
        return ImbalancedConfiguration.RL;
      }
    }
  }
}
