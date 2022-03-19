package com.github.adamtmalek.flightsimulator.gui;

import com.github.adamtmalek.flightsimulator.gui.dialog.DialogResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class OpenFileViewControllerImpl implements OpenFileViewController {
	@Override
	public @NotNull Optional<FlightFilesPaths> openDialog(@Nullable Component dialogParentView) {
		final var directory = getDataDirectoryWithFileChooser(dialogParentView);
		if (directory.isEmpty()) return Optional.empty();

		final var chosenDirectory = directory.get();
		final var csvFiles = getCsvFilesInDirectory(chosenDirectory);
		if (!hasDirectoryGotMinimumCsvFiles(csvFiles)) {
			JOptionPane.showMessageDialog(new JFrame(), "Directory has to contain at least 4 CSV files", "Error", JOptionPane.ERROR_MESSAGE);
			return Optional.empty();
		}

		return getFlightFilesPaths(chosenDirectory, csvFiles);
	}

	private @NotNull List<Path> getCsvFilesInDirectory(@NotNull File directory) {
		return Arrays.stream(Objects.requireNonNull(directory.listFiles(f -> f.getName().endsWith(".csv"))))
				.map(File::toPath)
				.toList();
	}

	private boolean hasDirectoryGotMinimumCsvFiles(@NotNull Collection<Path> csvFiles) {
		return csvFiles.size() >= 4;
	}

	private @NotNull Optional<File> getDataDirectoryWithFileChooser(@Nullable Component dialogParentView) {
		final var fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(dialogParentView) != JFileChooser.APPROVE_OPTION)
			return Optional.empty();
		else
			return Optional.of(fileChooser.getSelectedFile());
	}

	@NotNull
	private Optional<FlightFilesPaths> getFlightFilesPaths(@NotNull File chosenDirectory,
																												 @NotNull Collection<Path> csvFiles) {
		final var filePicker = new FilePicker(chosenDirectory.toPath());
		selectBestCandidatesForEachFile(filePicker, csvFiles);
		return getResultFromDialog(filePicker);
	}

	private void selectBestCandidatesForEachFile(@NotNull OpenFileView view, @NotNull Collection<Path> files) {
		setSelectedPathCandidate(view::setAeroplanesFileSelectionTo, files, "aeroplane");
		setSelectedPathCandidate(view::setAirlinesFileSelectionTo, files, "airline");
		setSelectedPathCandidate(view::setAirportsFileSelectionTo, files, "airport");
		setSelectedPathCandidate(view::setFlightsFileSelectionTo, files, "flight");
	}

	private void setSelectedPathCandidate(@NotNull Consumer<Path> valueSetter,
																				@NotNull Collection<Path> candidates,
																				@NotNull String keyName) {
		candidates.stream()
				.filter(e -> e.getFileName().toString().contains(keyName))
				.findFirst()
				.ifPresent(valueSetter);
	}

	private Optional<FlightFilesPaths> getResultFromDialog(@NotNull FilePicker filePicker) {
		if (filePicker.showDialog() == DialogResult.Cancelled) return Optional.empty();

		final var aeroplanes = Objects.requireNonNull(filePicker.getSelectedAeroplanesFile());
		final var airlines = Objects.requireNonNull(filePicker.getSelectedAirlinesFile());
		final var airports = Objects.requireNonNull(filePicker.getSelectedAirportsFile());
		final var flights = Objects.requireNonNull(filePicker.getSelectedFlightsFile());

		return Optional.of(new FlightFilesPaths(aeroplanes, airlines, airports, flights));
	}
}
