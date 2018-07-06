package org.matsim.contrib.josm.gui;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.preferences.projection.ProjectionChoice;
import org.openstreetmap.josm.gui.preferences.projection.ProjectionPreference;
import org.openstreetmap.josm.tools.GBC;
import org.openstreetmap.josm.tools.ImageProvider;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

/**
 * the import dialog
 *
 * @author Andreas, Template by Nico
 *
 */
@SuppressWarnings("serial")
public class LinkReferenceDialog extends JPanel {

	/**
	 * Holds the path of the import file
	 */
	final JLabel populationHeading = new JLabel("Population/Plans:");
	final JLabel populationPath = new JLabel("...");
	final JButton populationPathButton = new JButton(new ImageProvider("open.png").getResource().getImageIcon(new Dimension(10, 10)));
	final JLabel chainPathHeading = new JLabel("Link Chains:");
	final JLabel chainPath = new JLabel("...");
	final JButton chainPathButton = new JButton(new ImageProvider("open.png").getResource().getImageIcon(new Dimension(10, 10)));

	private File populationFile = null;
	private File chainFile = null;

	public LinkReferenceDialog() {

		populationHeading.setFont(populationHeading.getFont().deriveFont(Font.BOLD));
		chainPathHeading.setFont(populationPath.getFont().deriveFont(Font.BOLD));
		populationPath.setBorder(BorderFactory.createEtchedBorder());
		chainPath.setBorder(BorderFactory.createEtchedBorder());

		setLayout(new GridBagLayout());

		add(populationHeading, GBC.eop());
		add(populationPath, GBC.eop().fill(GridBagConstraints.HORIZONTAL) );
		add(populationPathButton, GBC.eop());

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setPreferredSize(new Dimension(400,3));
		add(sep, GBC.eop());

		add(chainPathHeading, GBC.eop());
		add(chainPath, GBC.eop().fill(GridBagConstraints.HORIZONTAL) );
		add(chainPathButton, GBC.eop());

		populationPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser;
				if(populationFile == null) {
					chooser = new JFileChooser(System.getProperty("user.home"));
				} else {
					chooser = new JFileChooser(populationFile.getAbsolutePath());
				}
				chooser.setApproveButtonText("Select");
				chooser.setDialogTitle("MATSim-LinkReference");
				FileFilter filter = new FileNameExtensionFilter("population-XML", "xml");
				chooser.setFileFilter(filter);
				int result = chooser.showOpenDialog(Main.parent);
				if (result == JFileChooser.APPROVE_OPTION) {
					populationFile = new File(chooser.getSelectedFile().getAbsolutePath());
					populationPath.setText(populationFile.getName());
				}
			}
		});

		chainPathButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser;
				if(chainFile == null) {
					chooser = new JFileChooser(System.getProperty("user.home"));
				} else {
					chooser = new JFileChooser(populationFile.getAbsolutePath());
				}
				chooser.setApproveButtonText("Import");
				chooser.setDialogTitle("MATSim-Import");
				FileFilter filter = new FileNameExtensionFilter("linkChain-txt", "txt");
				chooser.setFileFilter(filter);
				int result = chooser.showOpenDialog(Main.parent);
				if (result == JFileChooser.APPROVE_OPTION) {
					chainFile = new File(chooser.getSelectedFile().getAbsolutePath());
					chainPath.setText(chainFile.getName());
				}
			}
		});

	}
	
	public File getpopulationFile() {
		return populationFile;
	}

	public File getchainFile() {
		return chainFile;
	}


}
