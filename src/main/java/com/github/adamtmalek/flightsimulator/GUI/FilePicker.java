package com.github.adamtmalek.flightsimulator.GUI;

import com.github.adamtmalek.flightsimulator.GUI.renderers.PathListCellRenderer;
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

public class FilePicker extends JDialog {
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;

	private JComboBox<Path> aeroplanesComboBox;
	private JComboBox<Path> airlinesComboBox;
	private JComboBox<Path> airportsComboBox;
	private JComboBox<Path> flightsComboBox;

	private @Nullable Path aeroplanesPath = null;
	private @Nullable Path airlinesPath = null;
	private @Nullable Path airportsPath = null;
	private @Nullable Path flightsPath = null;

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

	private void onOK() {
		aeroplanesPath = (Path) aeroplanesComboBox.getSelectedItem();
		airlinesPath = (Path) airlinesComboBox.getSelectedItem();
		airportsPath = (Path) airportsComboBox.getSelectedItem();
		flightsPath = (Path) flightsComboBox.getSelectedItem();

		setVisible(false);
		dispose();
	}

	private void onCancel() {
		aeroplanesPath = null;
		airlinesPath = null;
		airportsPath = null;
		flightsPath = null;

		setVisible(false);
		dispose();
	}

	public @Nullable SelectedPaths showDialog() {
		setVisible(true);

		if (aeroplanesPath == null || airlinesPath == null
				|| airportsPath == null || flightsPath == null) {
			return null;
		} else {
			return new SelectedPaths(aeroplanesPath, airlinesPath, airportsPath, flightsPath);
		}
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

		setSelectedPathCandidate(aeroplanesComboBox, files, "aeroplane");
		setSelectedPathCandidate(airlinesComboBox, files, "airline");
		setSelectedPathCandidate(airportsComboBox, files, "airport");
		setSelectedPathCandidate(flightsComboBox, files, "flight");
	}

	private void setSelectedPathCandidate(@NotNull JComboBox<Path> comboBox,
																				@NotNull Collection<Path> candidates,
																				@NotNull String keyName) {
		candidates.stream()
				.filter(e -> e.getFileName().toString().contains(keyName))
				.findFirst()
				.ifPresent(comboBox::setSelectedItem);
	}

	public record SelectedPaths(@NotNull Path aeroplanes,
															@NotNull Path airlines,
															@NotNull Path airports,
															@NotNull Path flights) {

	}
}
