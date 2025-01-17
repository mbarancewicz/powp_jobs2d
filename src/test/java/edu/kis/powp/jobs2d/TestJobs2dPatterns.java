package edu.kis.powp.jobs2d;

import edu.kis.legacy.drawer.panel.DefaultDrawerFrame;
import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.ComplexCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;
import edu.kis.powp.jobs2d.drivers.adapter.DrawerAdapter;
import edu.kis.powp.jobs2d.drivers.adapter.LineDrawerAdapter;
import edu.kis.powp.jobs2d.events.SelectChangeVisibleOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigureOptionListener;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.magicpresets.FiguresJoe;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.kis.powp.jobs2d.utils.Point.point;

public class TestJobs2dPatterns {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * Setup test concerning preset figures in context.
	 * 
	 * @param application Application context.
	 */
	private static void setupPresetTests(Application application) {
		SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager());
		SelectTestFigureOptionListener selectTestFigure2OptionListener = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager());
		SelectTestFigureOptionListener selectTestFigureCommandOptionListener = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager());
		SelectTestFigureOptionListener selectTestFigureCommandSquareOptionListener = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager());
		SelectTestFigureOptionListener selectTestFigureCommandTriangleOptionListener = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager());

		selectTestFigureOptionListener.addAction(() ->
				FiguresJoe.figureScript1(selectTestFigureOptionListener.getDriverManager().getCurrentDriver()));

		selectTestFigure2OptionListener.addAction(() ->
				FiguresJoe.figureScript2(selectTestFigure2OptionListener.getDriverManager().getCurrentDriver()));

		selectTestFigureCommandOptionListener.addAction(() -> {
			ComplexCommand complexCommand = new ComplexCommand();
			complexCommand.addCommand(new SetPositionCommand(selectTestFigureCommandOptionListener.getDriverManager().getCurrentDriver(), 120, 120))
			.addCommand(new OperateToCommand(selectTestFigureCommandOptionListener.getDriverManager().getCurrentDriver(), 60, 120))
			.addCommand(new OperateToCommand(selectTestFigureCommandOptionListener.getDriverManager().getCurrentDriver(), 90, 60))
			.addCommand(new OperateToCommand(selectTestFigureCommandOptionListener.getDriverManager().getCurrentDriver(), 120, 120))
			.execute();
		});

		selectTestFigureCommandSquareOptionListener.addAction(() ->
				ComplexCommand.createSquare(selectTestFigureCommandSquareOptionListener.getDriverManager().getCurrentDriver(),
						100, 100, 50).execute());

		selectTestFigureCommandTriangleOptionListener.addAction(() ->
				ComplexCommand.createFigure(selectTestFigureCommandTriangleOptionListener.getDriverManager().getCurrentDriver(),
				List.of(point(50, 50),
						point(50, 25),
						point(75, 50))).execute());

		application.addTest("Figure Joe 1", selectTestFigureOptionListener);
		application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
		application.addTest("Test complex command", selectTestFigureCommandOptionListener);
		application.addTest("Test square command factory", selectTestFigureCommandSquareOptionListener);
		application.addTest("Test figure command factory", selectTestFigureCommandTriangleOptionListener);
	}

	/**
	 * Setup driver manager, and set default driver for application.
	 * 
	 * @param application Application context.
	 */
	private static void setupDrivers(Application application) {
		Job2dDriver loggerDriver = new LoggerDriver();
		DriverFeature.addDriver("Logger Driver", loggerDriver);
		DriverFeature.getDriverManager().setCurrentDriver(loggerDriver);

		Job2dDriver testDriver = new DrawerAdapter(DrawerFeature.getDrawerController());
		DriverFeature.addDriver("Buggy Simulator", testDriver);

		Job2dDriver specialDriver = new LineDrawerAdapter(DrawerFeature.getDrawerController(), LineFactory::getSpecialLine);
		DriverFeature.addDriver("Special Simulator", specialDriver);

		DriverFeature.updateDriverInfo();
	}

	/**
	 * Auxiliary routines to enable using Buggy Simulator.
	 * 
	 * @param application Application context.
	 */
	private static void setupDefaultDrawerVisibilityManagement(Application application) {
		DefaultDrawerFrame defaultDrawerWindow = DefaultDrawerFrame.getDefaultDrawerFrame();
		application.addComponentMenuElementWithCheckBox(DrawPanelController.class, "Default Drawer Visibility",
				new SelectChangeVisibleOptionListener(defaultDrawerWindow), true);
		defaultDrawerWindow.setVisible(true);
	}

	/**
	 * Setup menu for adjusting logging settings.
	 * 
	 * @param application Application context.
	 */
	private static void setupLogger(Application application) {
		application.addComponentMenu(Logger.class, "Logger", 0);
		application.addComponentMenuElement(Logger.class, "Clear log",
				(ActionEvent e) -> application.flushLoggerOutput());
		application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
		application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
		application.addComponentMenuElement(Logger.class, "Warning level",
				(ActionEvent e) -> logger.setLevel(Level.WARNING));
		application.addComponentMenuElement(Logger.class, "Severe level",
				(ActionEvent e) -> logger.setLevel(Level.SEVERE));
		application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Application app = new Application("2d jobs Visio");
				DrawerFeature.setupDrawerPlugin(app);
				setupDefaultDrawerVisibilityManagement(app);

				DriverFeature.setupDriverPlugin(app);
				setupDrivers(app);
				setupPresetTests(app);
				setupLogger(app);

				app.setVisibility(true);
			}
		});
	}

}
