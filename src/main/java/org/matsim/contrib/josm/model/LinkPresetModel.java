package org.matsim.contrib.josm.model;

import java.util.List;
import java.util.LinkedList;

import javafx.collections.ObservableList;

@SuppressWarnings("restriction")
public class LinkPresetModel {

//	private ObservableList<LinkPreset> presets;
	private final List<LinkPreset> presets = new LinkedList<>();
	
	public LinkPresetModel() {
		presets.add(new LinkPreset("test","13.33","1200","1","car;walk","0"));
		presets.add(new LinkPreset("test_","8.88","400","1","walk;bike","0"));
	}
	
	/**
	 * Get LinkPreset with index i
	 */
	public LinkPreset getLinkPreset(int i) {
		return presets.get(i);
	}
	
	/**
	 * Get list of Presets
	 */

	public List<LinkPreset> getPresets() {
		
		return presets;
	}
	
	/**
	 * Add new LinkPreset to List
	 */
	public void addLinkPreset(String[] in) {
		presets.add(new LinkPreset(in[0],in[1],in[2],in[3],in[4],in[5]));
	}
	
	/**
	 * Save the Presets to preferences
	 */
	public void savePrefs() {
		// TODO
	}
	
	/**
	 * Load the Presets from preferences
	 */
	public void loadPrefs() {
		// TODO
	}
	
	/**
	 * Load the Presets from extermal file
	 */
	public void loadExt() {
		// TODO
	}
	
	/**
	 * Save the Presets to extermal file
	 */
	public void saveExt() {
		// TODO
	}
	
	
}
