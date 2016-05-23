public Node createTreeForJson(Node node, Tree tree) {
		if (tree.isLeaf()) {
			if (tree.label() != null) {
				node.setName(tree.label().value());
			}
		} else {
			SimpleMatrix vector = RNNCoreAnnotations.getPredictions(tree);
			if(vector != null){
				for(int i=0;i<vector.getNumElements();i++){
					node.getProb().add(vector.get(i));
				}	
			}
			
			if (tree.value() != null && tree.label() != null) {
				node.setName(tree.label().value());
			}
			Tree[] children = tree.children();
			if (children != null) {
				for (Tree child : children) {
					Node childNode = new Node();
					node.getChildren().add(childNode);
					createTreeForJson(childNode, child);
				}
			}
		}
		return node;
	}
