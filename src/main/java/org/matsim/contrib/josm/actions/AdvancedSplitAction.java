package org.matsim.contrib.josm.actions;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.ChangeNodesCommand;
import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.TagMap;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.tools.Shortcut;

@SuppressWarnings("serial")
public class AdvancedSplitAction extends JosmAction {

	public AdvancedSplitAction() {
		super(tr("Advanced Way Split"), "splitway.png", tr("Split a way and refactor."),
				Shortcut.registerShortcut("tools:advsplitway", tr("Tool: {0}", tr("Advanced Split Way")), KeyEvent.VK_A,
						Shortcut.ALT_CTRL),
				true);
	}

	/**
	 * Called when the action is executed.
	 *
	 * This method performs a way-split so that the MATSim model constraints are
	 * satisfied. It splits the way, recalculates way length, refactors IDs and
	 * writes the channgelog into a external File for further use in correcting
	 * facilities and agent plans.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/**
		 * 1) Check if Selection is a way 2) Check if way has MATSim Preset 3) Check if
		 * Selection has breakpoints
		 * 
		 * 4) Split way into n Pieces 5) assign new ID {_0;_1;_2;...} 6) write
		 * changetrack into file 7) Get them ways into the layer
		 * 
		 */
		// 1) Check if selection contains ways and fetch them
		DataSet ds = getLayerManager().getEditDataSet();
		Collection<OsmPrimitive> selection = ds.getSelected();
		List<Way> selectedWays = OsmPrimitive.getFilteredList(selection, Way.class);
		if (selectedWays.isEmpty()) {
			return;
		}

		// 2) Check if ways have MATSim Id
		// 3) Check if ways have breakpoints
		List<Way> chosenWays = new ArrayList<>();
		for (Way w : selectedWays) {
			if (w.getNodesCount() > 2 && w.getKeys().get("matsim:id") != null) {
				chosenWays.add(w);
			}
		}

		// 4) Split ways into n pieces with n=#Nodes
		List<Way> newWays = new ArrayList<>();
		Collection<Command> commandList = new ArrayList<>();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("NewLinkChains.txt"),true))) {
			String chain = "";

			for (Way w : chosenWays) {
				chain = w.getKeys().get("matsim:id")+" ";
				for (int i = 1; i <= w.getNodesCount() - 1; i++) {
					// get current Chunk
					List<Node> currentChunk = new ArrayList<>();
					currentChunk.add(w.getNode(i - 1));
					currentChunk.add(w.getNode(i));

					if (i == 1) {
						commandList.add(new ChangeNodesCommand(ds, w, currentChunk));
						commandList.add(new ChangePropertyCommand(w, "matsim:length", null));
						continue;
					}

					// 5) make new Way with new ID and chunk as Nodes
					Way newWay = new Way();
					newWay.setNodes(currentChunk);

					TagMap tm = w.getKeys();

					tm.put("matsim:id", tm.get("matsim:id") + "_" + (i - 1));
					tm.remove("matsim:length");
					newWay.setKeys(tm);
					chain = chain+tm.get("matsim:id") + " ";

					commandList.add(new AddCommand(ds, newWay));

					newWays.add(newWay);
				}
				writer.write(chain+"\n");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 7) Get them ways into the layer
		MainApplication.undoRedo.add(new SequenceCommand(tr("Advanced Split"), commandList));
	}
}
