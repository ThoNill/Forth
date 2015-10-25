package forth.gui;

import javafx.collections.ObservableList;

import javafx.scene.control.ListView;
import toni.forth.Dictionary;
import toni.forth.IntStack;

public class ListConverter {

	static public ListView<String> getList(IntStack stack,IntegerConverter converter) {
		ListView<String> list = new ListView<>();

		ObservableList<String> items = list.getItems();
		for (int i = stack.size() - 1; i >= 0; i--) {
			items.add(converter.convert(stack.peek(i)));
		}

		return list;
	}
	
	
	static public void update(ListView<String> list, IntStack stack,IntegerConverter converter) {
//	if (stack.isDirty()) {
			ObservableList<String> items = list.getItems();
			items.clear();
			for (int i = stack.size() - 1; i >= 0; i--) {
				items.add(converter.convert(stack.peek(i)));
			}
	//	}

	}

	static public ListView<String> getList(IntStack stack) {
		return getList(stack, new SimpleIntegerConverter());
	}
	
	static public void update(ListView<String> list, IntStack stack) {
		update(list,stack, new SimpleIntegerConverter());
	}

	static public ListView<String> getList(Dictionary dict) {
		ListView<String> list = new ListView<>();

		ObservableList<String> items = list.getItems();
		for (int i = dict.getTopHeap() - 1; i >= 0; i--) {
			items.add(dict.getDescription(i));
		}

		return list;
	}

	static public void update(ListView<String> list, Dictionary dict) {
		
		ObservableList<String> items = list.getItems();
		items.clear();
		for (int i = dict.getTopHeap() - 1; i >= 0; i--) {
			items.add(dict.getDescription(i));
		}
		

	}

}
