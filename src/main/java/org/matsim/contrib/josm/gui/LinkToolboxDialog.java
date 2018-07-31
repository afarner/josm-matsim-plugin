package org.matsim.contrib.josm.gui;

import java.awt.Dimension;
import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.matsim.contrib.josm.model.LinkPreset;
import org.matsim.contrib.josm.model.LinkPresetModel;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeEvent;
import org.openstreetmap.josm.gui.layer.MainLayerManager.ActiveLayerChangeListener;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.util.HighlightHelper;
import org.openstreetmap.josm.spi.preferences.PreferenceChangeEvent;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;

/**
 * The ToggleDialog that opens the LinkToolbox. Here, LinkPresets can be picked
 * to then use with the LinkDrawAction. This creates ways and automatically
 * assigns them the attributes defined in the LinkPreset.
 * 
 *
 * @author Andreas
 */

@SuppressWarnings({ "serial", "restriction", "unchecked" })
public class LinkToolboxDialog extends ToggleDialog implements ActiveLayerChangeListener {
	private final JFXPanel LtPanel;
	private final JFXPanel fxPanel;
	private final JFXPanel attPanel;
	private final TableView<LinkPreset> table_lp;
	private final StringProperty title;
	
	private final LinkPresetModel model = new LinkPresetModel();

	ComboBox fsBox;
	ComboBox capBox;
	ComboBox plBox;
	ComboBox modeBox;
	CheckBox oneBox;
	
	Button addButton;
	Button delButton;
	Button impButton;
	Button expButton;
	
	
	@Override
	public void showNotify() {
		MainApplication.getLayerManager().addAndFireActiveLayerChangeListener(this);

	}

	@Override
	public void hideNotify() {

		MainApplication.getLayerManager().removeActiveLayerChangeListener(this);
	}

	public LinkToolboxDialog() {
		super("LinkToolbox", "addsegment.png", "LinkToolbox", null, 150, true, Preferences.class);
		Platform.setImplicitExit(false); // http://stackoverflow.com/questions/29302837/javafx-platform-runlater-never-running
		
		// ----- Create Preset Table ------
		
		fxPanel = new JFXPanel();
		table_lp = new TableView<>();
		ObservableList<LinkPreset> presets = FXCollections.observableList(model.getPresets());
		table_lp.setItems(presets);
		title = new SimpleStringProperty("LinkToolbox");
//		createLayout(fxPanel, false, null);
		
		Platform.runLater(() -> {
			title.addListener((InvalidationListener) -> SwingUtilities.invokeLater(() -> setTitle(title.get())));
			TableColumn<LinkPreset, String> nameColumn = new TableColumn<>("name");
			nameColumn.setCellValueFactory(r -> r.getValue().nameProperty());
			nameColumn.setResizable(true);
			TableColumn<LinkPreset, String> freespeedColumn = new TableColumn<>("fs");
			freespeedColumn.setCellValueFactory(r -> r.getValue().freespeedProperty());
			freespeedColumn.setResizable(false);
			freespeedColumn.setPrefWidth(90);
			TableColumn<LinkPreset, String> capacityColumn = new TableColumn<>("cap");
			capacityColumn.setCellValueFactory(r -> r.getValue().capacityProperty());
			capacityColumn.setResizable(false);
			capacityColumn.setPrefWidth(90);
			TableColumn<LinkPreset, String> permlanesColumn = new TableColumn<>("pl");
			permlanesColumn.setCellValueFactory(r -> r.getValue().permlanesProperty());
			permlanesColumn.setResizable(false);
			permlanesColumn.setPrefWidth(30);
			TableColumn<LinkPreset, String> modeColumn = new TableColumn<>("mode");
			modeColumn.setCellValueFactory(r -> r.getValue().modeProperty());
			modeColumn.setResizable(true);
			capacityColumn.setPrefWidth(90);
			TableColumn<LinkPreset, String> onewayColumn = new TableColumn<>("one");
			onewayColumn.setCellValueFactory(r -> r.getValue().onewayProperty());
			onewayColumn.setResizable(false);
			onewayColumn.setPrefWidth(30);

			table_lp.getColumns().setAll(nameColumn, freespeedColumn, capacityColumn, modeColumn, permlanesColumn,
					onewayColumn);
			table_lp.setRowFactory(v -> {
				TableRow<LinkPreset> row = new TableRow<>();
				return row;
			});

//			HighlightHelper highlightHelper = new HighlightHelper();
//			table_lp.getSelectionModel().getSelectedItems().addListener(new InvalidationListener() {
//				@Override
//				public void invalidated(Observable observable) {
//					if (MainApplication.getLayerManager().getEditDataSet() != null) {
//						highlightHelper.clear();
//						MainApplication.getLayerManager().getEditDataSet().clearHighlightedWaySegments();
//						// for (LinkPreset preset : table_lp.getSelectionModel().getSelectedItems()) {
//						// highlightHelper.highlight(route.getRelation().getMemberPrimitivesList());
//						// }
//						MainApplication.getMap().mapView.repaint();
//					}
//				}
//			});
			AnchorPane root = new AnchorPane();
			AnchorPane.setTopAnchor(table_lp, 0.0);
			AnchorPane.setLeftAnchor(table_lp, 0.0);
			AnchorPane.setRightAnchor(table_lp, 0.0);
			AnchorPane.setBottomAnchor(table_lp, 0.0);
			root.getChildren().add(table_lp);
			Scene scene = new Scene(root);
			fxPanel.setScene(scene);
		});
		
		// ----- Create Attribute Dialog ------
		
		attPanel = new JFXPanel();
		
		// -- LabelPanel with Boxes --
		GridPane labelPane = new GridPane();
		labelPane.setPadding(new javafx.geometry.Insets(5,5,5,5));
		labelPane.setVgap(4);
		labelPane.setHgap(4);
		
		 	
		
		labelPane.add(new Label("freespeed"),0,0);
		fsBox = new ComboBox();
		fsBox.setEditable(true);
		labelPane.add(fsBox,1,0);
		
		labelPane.add(new Label("capacity"),0,1);
		capBox = new ComboBox();
		labelPane.add(capBox,1,1); 
		capBox.setEditable(true);
		
		labelPane.add(new Label("permlanes"),0,2);
		plBox = new ComboBox();
		labelPane.add(plBox,1,2); 
		plBox.setEditable(true);
		
		labelPane.add(new Label("mode"),0,3);
		modeBox = new ComboBox();
		labelPane.add(modeBox,1,3); 
		modeBox.setEditable(true);
		
		labelPane.add(new Label("oneway"),0,4);
		oneBox = new CheckBox();
		labelPane.add(oneBox,1,4);
		
		labelPane.setPrefWidth(240);
		
		// -- SidePanel with buttons --
		GridPane sidePane = new GridPane();
		sidePane.setPadding(new javafx.geometry.Insets(5,5,5,5));
		sidePane.setVgap(4);
		sidePane.setHgap(4);
		
		addButton = new Button("add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	System.out.println(fsBox.getValue());
		    	System.out.println(fsBox==null);
		    	System.out.println("Hello world. I'm the add button!");
		        // TODO Check for consistency and add LinkPreset
		    }
		});
		delButton = new Button("delete");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        // TODO Delete selected Preset
		    }
		});
		impButton = new Button("import Presets");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        // TODO open import dialog
		    }
		});
		expButton = new Button("export Presets");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        // TODO open export dialog
		    }
		});
		sidePane.add(addButton,0,0);
		sidePane.add(delButton,1,0);
		sidePane.add(impButton,0,2,2,1);
		sidePane.add(expButton,0,3,2,1);
		
		// -- Set the scene --
		HBox hbox = new HBox();
		hbox.getChildren().addAll(labelPane,sidePane);
		attPanel.setScene(new Scene(hbox));
		attPanel.setVisible(true);
	
		
		
		// ----- Create Content Pane ------
		
		LtPanel = new JFXPanel();
		LtPanel.setLayout(new BoxLayout(LtPanel, BoxLayout.Y_AXIS));
		
		JLabel presetTitle = new JLabel("Link Presets");
		presetTitle.setAlignmentX(0);
		presetTitle.setHorizontalAlignment(SwingConstants.LEFT);
		presetTitle.setHorizontalTextPosition(SwingConstants.LEFT);

		JLabel attributesTitle = new JLabel("Attributes");
		presetTitle.setAlignmentX(0);
//		attributesTitle.setHorizontalAlignment(SwingConstants.LEFT);
		
		LtPanel.add(presetTitle);
		
		fxPanel.setAlignmentX(0);
		LtPanel.add(fxPanel);
		
		LtPanel.add(attributesTitle);
		
		attPanel.setAlignmentX(0);
		attPanel.setMinimumSize(new Dimension(200,150));
		LtPanel.add(attPanel);

		createLayout(LtPanel, false, null);
		
		
	}

	public void init() {
		enabledness();
	}

	@Override
	public void activeOrEditLayerChanged(ActiveLayerChangeEvent e) {
		OsmDataLayer editLayer = MainApplication.getLayerManager().getEditLayer();

	}

	@Override
	public void preferenceChanged(PreferenceChangeEvent preferenceChangeEvent) {
		super.preferenceChanged(preferenceChangeEvent);
		if (preferenceChangeEvent.getKey().equalsIgnoreCase("matsim_supportTransit")) {
			enabledness();
		}
	}

	private void enabledness() {
		boolean enabled = Preferences.isSupportTransit();
		getButton().setEnabled(enabled);
		if (isShowing() && !enabled) {
			hideDialog();
			hideNotify();
		}
	}

	private void createButtons() {
		Button addButton = new Button("add");
		addButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        System.out.println("Hello, i'm the add Button. Pleasure to meet you!");
		    }
		});
	}
	
}
