package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.gui.dialog.Dialog;
import com.github.adamtmalek.flightsimulator.gui.renderers.PathListCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class FilePicker extends Dialog implements OpenFileView {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;

	private JComboBox<Path> aeroplanesComboBox;
	private JComboBox<Path> airlinesComboBox;
	private JComboBox<Path> airportsComboBox;
	private JComboBox<Path> flightsComboBox;

	public FilePicker(@NotNull Path pickedDirectory) {
		setContentPane(contentPane);
		setTitle("Open Files");
		setBounds(0, 0, 300, 300);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		buttonOK.addActionListener(e -> onOK());

		buttonCancel.addActionListener(e -> onCancel());

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		final var files = Arrays.stream(Objects.requireNonNull(new File(pickedDirectory.toUri())
						.listFiles()))
				.map(File::toPath)
				.toList();
		initComponents(files);
	}

	@Override
	public @Nullable Path getSelectedAeroplanesFile() {
		return (Path) aeroplanesComboBox.getSelectedItem();
	}

	@Override
	public @Nullable Path getSelectedAirlinesFile() {
		return (Path) airlinesComboBox.getSelectedItem();
	}

	@Override
	public @Nullable Path getSelectedAirportsFile() {
		return (Path) airportsComboBox.getSelectedItem();
	}

	@Override
	public @Nullable Path getSelectedFlightsFile() {
		return (Path) flightsComboBox.getSelectedItem();
	}

	@Override
	public void setAeroplanesFileSelectionTo(@NotNull Path value) {
		aeroplanesComboBox.setSelectedItem(value);
	}

	@Override
	public void setAirlinesFileSelectionTo(@NotNull Path value) {
		airlinesComboBox.setSelectedItem(value);
	}

	@Override
	public void setAirportsFileSelectionTo(@NotNull Path value) {
		airportsComboBox.setSelectedItem(value);
	}

	@Override
	public void setFlightsFileSelectionTo(@NotNull Path value) {
		flightsComboBox.setSelectedItem(value);
	}

	private void createUIComponents() {
		aeroplanesComboBox = new JComboBox<>();
		airlinesComboBox = new JComboBox<>();
		airportsComboBox = new JComboBox<>();
		flightsComboBox = new JComboBox<>();

		Stream.of(aeroplanesComboBox, airlinesComboBox, airportsComboBox, flightsComboBox)
				.forEach(comboBox -> comboBox.setRenderer(new PathListCellRenderer()));
	}

	private void initComponents(@NotNull Collection<Path> files) {
		Stream.of(aeroplanesComboBox, airlinesComboBox, airportsComboBox, flightsComboBox)
				.forEach(comboBox -> {
					final var model = new DefaultComboBoxModel<Path>();
					model.addAll(files);
					comboBox.setModel(model);
				});
	}
}
