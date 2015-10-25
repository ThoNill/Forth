package forth.gui;

import java.io.IOException;

import toni.forth.MiniForth;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

	enum Status {
		 INIT,START, STOP;
	}

	private MiniForth forth;
	private ListView<String> datenStackView;
	private ListView<String> returnStackView;
	private ListView<String> programmStackView;
	private ListView<String> dictionaryView;
	private Text fehlermeldung;
	private Paint originalColorOfFehlermmeldung;

	private TokenStreamWithPosition tokenStream = null;
	private TextArea programmText;
	private TextArea ausgabe;
	private Status status;

	public static void main(String args[]) {
		launch(args);
	}

	public void init() throws Exception {
		forth = new MiniForth(2000, 100, 100);
		
	}

	@Override
	public void start(Stage stage) throws Exception {

		createControls();

		setStatus(Status.INIT);
		
		VBox accordion = createRechteSeitee();
		VBox linkeSeite = createLinkeSeite();

		HBox hbox = new HBox();
		hbox.getChildren().add(linkeSeite);
		hbox.getChildren().add(accordion);
		hbox.getChildren().add(createExamples());

		Scene scene = new Scene(hbox);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();
	}

	protected VBox createLinkeSeite() {
		HBox buttons = createCommands();

		VBox linkeSeite = new VBox();
		linkeSeite.setPadding(new Insets(20, 10, 20, 20));
		linkeSeite.setSpacing(10);
		linkeSeite.getChildren().addAll(title("Programmtext", programmText),
				buttons, fehlermeldung, title("Ausgabe", ausgabe));
		return linkeSeite;
	}

	private HBox createCommands() {
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.getChildren().addAll(
				createButton("Einzelschritt", e -> einzelschritt()),
				createButton("Gesamtschritt", e -> gesamtschritt())
				);
		return hbox;
	}
	
	private TitledPane createExamples() {
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(20, 10, 20, 20));
		vbox.setSpacing(10);
		
		for(Example example : Example.values()) {
		vbox.getChildren().add(
				createButton(example.getDescription(),new ExamplesHandler(this, example)));
		}
		TitledPane pane = title("Programmbeispiele",vbox);
		pane.setPadding(new Insets(20, 10, 20, 20));
		return pane;
	}


	private VBox createRechteSeitee() {
		// richtige Accordions haben nur eine einfache Selektion, deshalb der
		// Weg über VBox

		VBox accordion = new VBox();
		accordion.setPadding(new Insets(20, 20, 20, 10));
		accordion.getChildren().addAll(title("Daten Stack", datenStackView),
				title("Return Stack", returnStackView),
				title("Programm Stack", programmStackView),
				title("Wörterbuch", dictionaryView));

		setzeSichtbarkeitDesAccordions(accordion);
		return accordion;
	}

	private void setzeSichtbarkeitDesAccordions(VBox accordion) {
		((TitledPane) accordion.getChildren().get(0)).setExpanded(true);
		for (int n = 1; n < accordion.getChildren().size(); n++) {
			((TitledPane) accordion.getChildren().get(n)).setExpanded(false);
		}
	}

	private void createControls() {
		datenStackView = ListConverter.getList(forth.getDatenStack());
		returnStackView = ListConverter.getList(forth.getReturnStack());
		programmStackView = ListConverter.getList(forth.getProgrammStack());
		dictionaryView = ListConverter.getList(forth.getDictionary());

		fehlermeldung = new Text();
		originalColorOfFehlermmeldung = fehlermeldung.fillProperty().get();
		
		programmText = new TextArea();

		ausgabe = new TextArea();
		ausgabe.editableProperty().set(false);
		forth.setOutput(new TextAreaOutput(ausgabe));
	}

	private TitledPane title(String titel, Node node) {
		return new TitledPane(titel, node);
	}

	private Button createButton(String label, EventHandler<ActionEvent> handler) {
		Button b = new Button(label);
		b.setOnAction(handler);
		return b;
	}

	public void einzelschritt() {
		try {
			
			statusWechselPrüfen();

			forth.sayNextWord();
			programmText.selectRange(tokenStream.getStartPosition(),
					tokenStream.getEndPosition());

			statusWechselPrüfen();

			oberflächeAktualisieren();
		
		} catch (RuntimeException e) {
			fehlermeldungAusgaben(e);
			setStatus(Status.STOP);
		}
	}
	
	public void gesamtschritt() {
		try {
			
			statusWechselPrüfen();

			forth.say(tokenStream);
					

			statusWechselPrüfen();

			oberflächeAktualisieren();
		
		} catch (RuntimeException e) {
			fehlermeldungAusgaben(e);
			setStatus(Status.STOP);
		}
	}

	protected void statusWechselPrüfen() {
		if (status == Status.INIT  || status == Status.STOP) {
			setStatus(Status.START);
		}
		if (status == Status.START && !tokenStream.isOpen()) {
			setStatus(Status.STOP);
		}
	}


	private void setStatus(Status status) {
		switch (status) {
		case INIT:
			textInitialisieren();
			programmText.setText(" : HalloWelt! \" Hallo Welt!\" TYPE ; HalloWelt! ");
			break;
		case START:
			abarbeitungStarten();
			break;
		case STOP:
			textInitialisieren();
			break;
		default:
			break;
		}
		this.status = status;
		oberflächeAktualisieren();
	}

	private void abarbeitungStarten() {
		tokenStream = new TokenStreamWithPosition(
				programmText.getText());
		forth.setTokenStream(tokenStream);
		
		programmText.editableProperty().set(false);

		fehlermeldung.fillProperty().setValue(originalColorOfFehlermmeldung);
		fehlermeldung.setText("");
	}
	
	private void fehlermeldungAusgaben(RuntimeException e) {
	
		fehlermeldung.fillProperty().setValue(Color.RED);
		fehlermeldung.setText(e.getLocalizedMessage());
	}

	private void textInitialisieren() {
		tokenStream = null;
		programmText.setText("");
		programmText.editableProperty().set(true);
	}

	protected void oberflächeAktualisieren() {
		ListConverter.update(datenStackView, forth.getDatenStack());
		ListConverter.update(returnStackView, forth.getReturnStack());
		ListConverter.update(programmStackView, forth.getProgrammStack(),new DictionaryConverter(forth.getDictionary()));
		ListConverter.update(dictionaryView, forth.getDictionary());
	}



class ExamplesHandler implements EventHandler<ActionEvent> {
	Main main; 
	Example example;
	
	public ExamplesHandler(Main main, Example example) {
		super();
		this.main = main;
		this.example = example;
	}
	
	@Override
	public void handle(ActionEvent arg0) {
		main.programmText.setText(example.getProgramm());
		
	}
}
}
