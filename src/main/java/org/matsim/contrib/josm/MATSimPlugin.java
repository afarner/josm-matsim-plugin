package org.matsim.contrib.josm;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.KeyEvent;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.matsim.contrib.josm.actions.AdvancedSplitAction;
import org.matsim.contrib.josm.actions.ConvertAction;
import org.matsim.contrib.josm.actions.DownloadAction;
import org.matsim.contrib.josm.actions.DownloadVBBAction;
import org.matsim.contrib.josm.actions.ImportAction;
import org.matsim.contrib.josm.actions.LinkReferenceAction;
import org.matsim.contrib.josm.actions.ModifyModeAction;
import org.matsim.contrib.josm.actions.NetworkExporter;
import org.matsim.contrib.josm.actions.NetworkTest;
import org.matsim.contrib.josm.actions.NewNetworkAction;
import org.matsim.contrib.josm.actions.OTFVisAction;
import org.matsim.contrib.josm.actions.ShapeExporter;
import org.matsim.contrib.josm.actions.TransitScheduleExportAction;
import org.matsim.contrib.josm.actions.TransitScheduleTest;
import org.matsim.contrib.josm.gui.LinksToggleDialog;
import org.matsim.contrib.josm.gui.LinkToolboxDialog;
import org.matsim.contrib.josm.gui.PTToggleDialog;
import org.matsim.contrib.josm.gui.Preferences;
import org.matsim.contrib.josm.gui.StopAreasToggleDialog;
import org.matsim.contrib.josm.model.OsmConvertDefaults;
import org.matsim.contrib.osm.CreateStopAreas;
import org.matsim.contrib.osm.IncompleteRoutesTest;
import org.matsim.contrib.osm.MasterRoutesTest;
import org.matsim.contrib.osm.RepairAction;
import org.matsim.contrib.osm.UpdateStopTags;
import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.data.osm.visitor.paint.MapRendererFactory;
import org.openstreetmap.josm.data.preferences.BooleanProperty;
import org.openstreetmap.josm.data.preferences.sources.ValidatorPrefHelper;
import org.openstreetmap.josm.data.validation.OsmValidator;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.download.DownloadSelection;
import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPreset;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetMenu;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetReader;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetSeparator;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.spi.preferences.Config;
import org.openstreetmap.josm.spi.preferences.PreferenceChangeEvent;
import org.openstreetmap.josm.spi.preferences.PreferenceChangedListener;
import org.xml.sax.SAXException;

import javafx.embed.swing.JFXPanel;

/**
 * This is the main class for the MATSim plugin.
 *
 * @see Plugin
 *
 * @author Nico
 *
 */
public class MATSimPlugin extends Plugin implements PreferenceChangedListener {

	public MATSimPlugin(PluginInformation info) {
		super(info);

		new JFXPanel(); // super-weird, but we get random deadlocks on OSX when we don't initialize
						// JavaFX early

		// add xml exporter for matsim data
		ExtensionFileFilter.addExporterFirst(new NetworkExporter());

		MainMenu menu = MainApplication.getMenu();

		JMenu jMenu1 = menu.addMenu(tr("OSM Repair"), tr("OSM Repair"), KeyEvent.VK_CIRCUMFLEX,
				menu.getDefaultMenuPos(), "OSM Repair Tools");
		jMenu1.add(new JMenuItem(new RepairAction(tr("Create Master Routes"), new MasterRoutesTest())));
		jMenu1.add(new JMenuItem(new RepairAction(tr("Check for Incomplete Routes"), new IncompleteRoutesTest())));
		jMenu1.add(new JMenuItem(new RepairAction("Update Stop Tags", new UpdateStopTags())));
		jMenu1.add(new JMenuItem(new RepairAction("Create Stop Areas", new CreateStopAreas())));

		JMenu jMenu2 = menu.addMenu(tr("MATSim"), tr("MATSim"), KeyEvent.VK_DIVIDE, menu.getDefaultMenuPos(),
				"MATSim Tools");
		jMenu2.add(new ImportAction());
		jMenu2.add(new NewNetworkAction());
		jMenu2.add(new ConvertAction());
		jMenu2.add(new ShapeExporter());
		jMenu2.add(new JSeparator());

		jMenu2.add(new DownloadAction());
		jMenu2.add(new DownloadVBBAction());
		jMenu2.add(new JSeparator());

		jMenu2.add(new RepairAction(tr("Validate TransitSchedule"), new TransitScheduleTest()));
		TransitScheduleExportAction transitScheduleExportAction = new TransitScheduleExportAction();
		Config.getPref().addPreferenceChangeListener(transitScheduleExportAction);
		jMenu2.add(transitScheduleExportAction);
		jMenu2.add(new JSeparator());

		jMenu2.add(new OTFVisAction());
		jMenu2.add(new JSeparator());

		jMenu2.add(new AdvancedSplitAction());
		jMenu2.add(new ModifyModeAction());
		jMenu2.add(new LinkReferenceAction());

		// read tagging preset
		Reader reader = new InputStreamReader(getClass().getResourceAsStream("matsimPreset.xml"));
		Collection<TaggingPreset> tps;
		try {
			tps = TaggingPresetReader.readAll(reader, true);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		for (TaggingPreset tp : tps) {
			if (!(tp instanceof TaggingPresetSeparator)) {
				MainApplication.getToolbar().register(tp);
			}
		}
		// AutoCompletionManager.cachePresets(tps);
		HashMap<TaggingPresetMenu, JMenu> submenus = new HashMap<>();
		for (final TaggingPreset p : tps) {
			JMenu m = p.group != null ? submenus.get(p.group) : MainApplication.getMenu().presetsMenu;
			if (p instanceof TaggingPresetSeparator) {
				m.add(new JSeparator());
			} else if (p instanceof TaggingPresetMenu) {
				JMenu submenu = new JMenu(p);
				submenu.setText(p.getLocaleName());
				((TaggingPresetMenu) p).menu = submenu;
				submenus.put((TaggingPresetMenu) p, submenu);
				m.add(submenu);
			} else {
				JMenuItem mi = new JMenuItem(p);
				mi.setText(p.getLocaleName());
				m.add(mi);
			}
		}

		// register map renderer
		if (new BooleanProperty("matsim_renderer", false).get()) {
			MapRendererFactory factory = MapRendererFactory.getInstance();
			factory.register(MapRenderer.class, "MATSim Renderer", "This is the MATSim map renderer");
			factory.activate(MapRenderer.class);
		}

		// register for preference changed events
		Config.getPref().addPreferenceChangeListener(this);
		OsmConvertDefaults.listen(Config.getPref());

		// load default converting parameters

		// register validators
		List<String> matsimTests = new ArrayList<>();
		OsmValidator.addTest(NetworkTest.class);
		matsimTests.add(NetworkTest.class.getName());

		// make sure MATSim Validators aren't executed before upload
		Config.getPref().putList(ValidatorPrefHelper.PREF_SKIP_TESTS_BEFORE_UPLOAD, matsimTests);
	}

	@Override
	public void addDownloadSelection(List<DownloadSelection> list) {

	}

	@Override
	public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
		if (newFrame != null) {
			MainApplication.getMap().addToggleDialog(new LinksToggleDialog());
			MainApplication.getMap().addToggleDialog(new LinkToolboxDialog());
			PTToggleDialog toggleDialog1 = new PTToggleDialog();
			MainApplication.getMap().addToggleDialog(toggleDialog1);
			toggleDialog1.init(); // after being added
			StopAreasToggleDialog toggleDialog2 = new StopAreasToggleDialog();
			MainApplication.getMap().addToggleDialog(toggleDialog2);
			toggleDialog2.init(); // after being added
		}
	}

	@Override
	public PreferenceSetting getPreferenceSetting() {
		return new Preferences.Factory().createPreferenceSetting();
	}

	@Override
	public void preferenceChanged(PreferenceChangeEvent e) {
		if (e.getKey().equalsIgnoreCase("matsim_renderer")) {
			MapRendererFactory factory = MapRendererFactory.getInstance();
			if (new BooleanProperty("matsim_renderer", false).get()) {
				factory.register(MapRenderer.class, "MATSim Renderer", "This is the MATSim map renderer");
				factory.activate(MapRenderer.class);
			} else {
				factory.activateDefault();
				factory.unregister(MapRenderer.class);
			}
		}
	}
}
