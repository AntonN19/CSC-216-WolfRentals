package edu.ncsu.csc216.business.view.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.ncsu.csc216.business.model.io.RentalReader;
import edu.ncsu.csc216.business.model.io.RentalWriter;
import edu.ncsu.csc216.business.model.stakeholders.DuplicateClientException;
import edu.ncsu.csc216.business.model.stakeholders.DuplicateRoomException;
import edu.ncsu.csc216.business.model.stakeholders.Landlord;
import edu.ncsu.csc216.business.model.stakeholders.PropertyManager;
import edu.ncsu.csc216.business.view.ui.utils.CalendarPicker;
import edu.ncsu.csc216.business.view.ui.utils.SpringUtilities;


/** 
 * Graphical user interface for the Wolf High-Rise Rental system.
 * 
 * @author Jo Perry
 */
public class RentalGUI extends JFrame implements ActionListener {
	
	/** ID number used for object serialization. */
	private static final long serialVersionUID = 1L;
	/** Title for top of GUI. */
	private static final String APP_TITLE = "Wolf High-Rise Rentals";

	// Menu strings and components
	/** Text for the File Menu. */
	private static final String FILE_MENU_TITLE = "File";
	/** Text for the New File menu item. */
	private static final String NEW_FILE_TITLE = "New";
	/** Text for the Load File menu item. */
	private static final String LOAD_FILE_TITLE = "Load";
	/** Text for the Save File menu item. */
	private static final String SAVE_FILE_TITLE = "Save";
	/** Text for the Quit Application menu item. */
	private static final String QUIT_TITLE = "Quit";
	/** Menu bar for the GUI that contains Menus. */
	private JMenuBar menuBar;
	/** Menu for the GUI. */
	private JMenu menu;
	/** Menu item for creating a new data set of rental units, clients, and leases. */
	private JMenuItem itemNewFile;
	/** Menu item for loading a file. */
	private JMenuItem itemLoadFile;
	/** Menu item for saving the list to a file. */
	private JMenuItem itemSaveFile;
	/** Menu item for quitting the program. */
	private JMenuItem itemQuit;  

	// Strings for panels, labels
	/** Title for rental units panel*/
	private static final String ALL_RENTAL_UNITS = "Rental Units";
	/** Title for clients panel */
	private static final String ALL_CLIENTS = "Clients";
	/** Title for panel for leases for the selected rental unit */
	private static final String UNIT_LEASES = "Leases for Selected Rental Unit";
	/** String to label panel for closing or changing service availability */
	private static final String CLOSE_REMOVE = "Close, Remove/Restore Service";
	/** Title for the add client panel */
	private static final String ADD_CLIENT = "Add Client";
	/** Title for the new rental unit data panel */
	private static final String NEW_RENTAL_UNIT = "Add Rental Unit";
	/** String  to label number of floors text field */
	private static final String FLOOR = "Floor:";
	/** String  to label number of rooms text field */
	private static final String ROOM =   "  Room:";
	/** String  to label capacity text field */
	private static final String CAPACITY = "  Capacity:";
	/** Title for the panel for leases for selected client */
	private static final String CLIENT_LEASES = "Leases for Selected Client";
	/** String to label rental unit kind choices */
	private static final String RENTAL_UNIT_KIND = "Kind of Rental Unit: ";
	/** String  to label add lease button */
	private static final String NEW_LEASE = "Add Lease for Selected Client and Rental Unit";
	/** String  to label duration button */
	private static final String DURATION = "Duration (office months, hotel weeks, conf room days):";
	/** String  to label add number of occupants button */
	private static final String NUM_OCCUPANTS = "Number of Occupants:";

	// Buttons, RadioButtons, labels

	/** Applies filters to the Rental Unit display */
	private JButton btnFilter = new JButton("Filter");
	/** Adds a new rental unit to the list of existing rental units */
	private JButton btnAddRentalUnit = new JButton("Add New Rental Unit");
	/** Cancels the selected lease (from the client's leases) */
	private JButton btnCancelLease = new JButton("Cancel Client Lease");
	/** Adds a new client */
	private JButton btnAddClient = new JButton("Add New Client");
	/** Creates a new lease */
	private JButton btnCreateLease = new JButton("Add New Lease");
	/** Labels for the radio buttons (filtering and creating new rental units) */
	private String[] rbtnKindRULabels = {"Any", "Office", "Hotel Suite", "Conference Room"};
	/** Radio buttons for specifying the kind of rental unit to create  */
	private JRadioButton[] rbtnNewKinds = new JRadioButton[3]; 
	/** Calendar picker for lease start date */
	private JButton btnStartDatePick = new JButton("Lease Start Date: ");
	
	/** Returns a rental unit to service */
	private JButton btnReturnToService = new JButton("Restore Unit to Service");
	/** Closes the selected rental unit */
	private JButton btnCloseUnit = new JButton("Close Unit & Cancel Associated Leases");
	/** Opens a calendar for the user to select an out-of-service date */
	private JButton btnServiceEndDate = new JButton("Out of Service Date: ");	
	/** Takes the selected rental unit out of service */
	private JButton btnRemoveFromService = new JButton("Remove Unit from Service"); 
	

	// Lists and Tables
	/** Model for list of rental units */
	private DefaultListModel<String> dlmRentalUnits = new DefaultListModel<String>();
	/** Model for leases for a selected rental unit */
	private DefaultListModel<String> dlmRentalUnitLeases = new DefaultListModel<String>();
	/** Model for list of clients */
	private DefaultListModel<String> dlmClients = new DefaultListModel<String>();
	/** Model for leases for a selected client */
	private DefaultListModel<String> dlmClientLeases = new DefaultListModel<String>();
	/** List of rental units */
	private JList<String> lstRentalUnits = new JList<String>(dlmRentalUnits);
	/** List of leases for a selected rental unit */
	private JList<String> lstRentalUnitLeases = new JList<String>(dlmRentalUnitLeases);
	/** List of all clients */
	private JList<String> lstClients = new JList<String>(dlmClients);
	/** List of leases for a selected client */
	private JList<String> lstClientLeases = new JList<String>(dlmClientLeases);
	/** List font */
	private Font displayFont = new Font("Courier", Font.PLAIN, 12);

	// Scroll panes to hold rental unit/client/lease information
	/** Scroll pane to hold rental unit display */
	private JScrollPane scrlPaneRentalUnits;
	/** Scroll pane to hold client display */
	private JScrollPane scrlClient;
	/** Scroll pane to hold display of leases for selected rental unit */
	private JScrollPane scrlRentalUnitLeases;
	/** Scroll pane to hold display of leases for selected client */
	private JScrollPane scrlClientLeases;


	// Rental Unit input text fields
	/** Floor of new rental unit */
	private JTextField txtFloor = new JTextField(10);
	/** Room  of new rental unit */
	private JTextField txtRoom = new JTextField(10);
	/** New rental unit capacity */
	private JTextField txtCapacity = new JTextField(10);
	/** Date to take selected unit out of service */
	private JTextField txtNoServiceDate = new JTextField(10);

	// Client input text fields
	/** Field for client name */
	private JTextField txtClientName = new JTextField(10);
	/** Field for client contact */
	private JTextField txtClientContact = new JTextField(10);
	/** Field for party size for new lease */
	private JTextField txtPartySize = new JTextField(10);
	/** Field for start date of new lease */
	private JTextField txtStartDate = new JTextField(10);
	/** Field for lease duration */
	private JTextField txtDuration = new JTextField(10);

	// Main panels
	/** Panel for top left (list of rental units) */
	private JPanel pnlRentalUnitsLeft = new JPanel(new BorderLayout());
	/** Panel for top right (list of leases for selected rental unit) */
	private JPanel pnlRentalUnitsRight = new JPanel(new BorderLayout());
	/** Panel for lower left (list of clients) */
	private JPanel pnlClientsLeft = new JPanel(new BorderLayout());
	/** Panel for lower right (list of leases for selected client) */
	private JPanel pnlClientsRight = new JPanel(new BorderLayout());
	/** Calendar frame */
	private final JFrame calFrame = new JFrame();

	// Filter status
	/** Set to true to display only available rental units, false otherwise */
	private boolean displayAvailableOnly = false;
	/** Kind of rental unit to display. Default is "A" for all */
	private String displaySpecificKindOnly = "A";
	
	// Backend and file I/O
	/** Back end manager (gets data from the GUI and provides resulting data to the GUI) */
	private Landlord mgr = PropertyManager.getInstance();
	/** Name of the file with rental data most recently used during the current run */
	private String mostRecentFileName = null;

	/**
	 * Main method to open the GUI.
	 * @param args  not used
	 */
	public static void main(String[] args) {
		new RentalGUI();
	}

	/**
	 * Constructor. Creates an empty GUI and sets closure activities.
	 */
	public RentalGUI() {
		// Set up general location, title, closing behavior
		setSize(1040, 750);
		setLocation(50, 50);
		setTitle(APP_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUpMenuBar();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				doExit();
			}
		});
		// Dress the GUI with components
		initializeGUI();
		setVisible(true);
	}

	/**
	 * Makes the GUI Menu bar that contains options for loading a file, starting/opening
	 * a new file, saving data to a file, and quitting the application.
	 */
	private void setUpMenuBar() {
		// Construct Menu items
		menuBar = new JMenuBar();
		menu = new JMenu(FILE_MENU_TITLE);
		itemNewFile = new JMenuItem(NEW_FILE_TITLE);
		itemLoadFile = new JMenuItem(LOAD_FILE_TITLE);
		itemSaveFile = new JMenuItem(SAVE_FILE_TITLE);
		itemQuit = new JMenuItem(QUIT_TITLE);
		itemNewFile.addActionListener(this);
		itemLoadFile.addActionListener(this);
		itemSaveFile.addActionListener(this);
		itemQuit.addActionListener(this);

		// Build Menu and add to GUI
		menu.add(itemNewFile);
		menu.add(itemLoadFile);
		menu.add(itemSaveFile);
		menu.add(itemQuit);
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}


	/**
	 * Initializes GUI components, puts them together, and adds listeners.
	 */
	private void initializeGUI() {
		setUpLists();
		setUpRentalUnitPanels();
		setUpClientPanels();
		putTogetherPanels(getContentPane());
		addListeners();
	}   

	/**
	 * Creates the lists and associated widgets for client, rental unit, and lease displays.
	 */
	private void setUpLists() {
		// Display for rental units
		scrlPaneRentalUnits = new JScrollPane(lstRentalUnits);
		scrlPaneRentalUnits.setPreferredSize(new Dimension(230, 100));
		lstRentalUnits.setFont(displayFont); 
		lstRentalUnits.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Display for single rental unit leases 
		scrlRentalUnitLeases = new JScrollPane(lstRentalUnitLeases);
		scrlRentalUnitLeases.setPreferredSize(new Dimension(230, 100));
		lstRentalUnitLeases.setFont(displayFont);
		lstRentalUnitLeases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Now the display for all clients
		scrlClient = new JScrollPane(lstClients);
		scrlClient.setPreferredSize(new Dimension(230, 100));
		lstClients.setFont(displayFont);
		lstRentalUnitLeases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Display for all client leases
		scrlClientLeases  = new JScrollPane(lstClientLeases);
		scrlClientLeases.setPreferredSize(new Dimension(230, 100)); 
		lstClientLeases.setFont(displayFont);
		lstClientLeases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/**
	 * Sets up panels for all rental units and leases for selected rental unit.
	 */
	private void setUpRentalUnitPanels() {
		// Decorate the panel borders
		pnlRentalUnitsLeft.setBorder(BorderFactory.createTitledBorder(ALL_RENTAL_UNITS));
		pnlRentalUnitsRight.setBorder(BorderFactory.createTitledBorder(UNIT_LEASES));

		JPanel pnlCloseRestore = new JPanel(new SpringLayout());
		pnlCloseRestore.add(btnCloseUnit);
		pnlCloseRestore.add(btnReturnToService);
		SpringUtilities.makeCompactGrid(pnlCloseRestore, 1, 2, 6, 0, 6, 2);
		JPanel pnlEndService = new JPanel(new SpringLayout());
		pnlEndService.add(btnServiceEndDate);
		txtNoServiceDate.setEditable(false);
		pnlEndService.add(txtNoServiceDate);
		pnlEndService.add(btnRemoveFromService);
		SpringUtilities.makeCompactGrid(pnlEndService, 1, 3, 6, 0, 6, 2);

		JPanel pnlRemoveRestoreService = new JPanel(new BorderLayout());
		pnlRemoveRestoreService.setBorder(BorderFactory.createTitledBorder(CLOSE_REMOVE));
		pnlRemoveRestoreService.add(pnlCloseRestore, BorderLayout.NORTH);
		
		pnlRemoveRestoreService.add(pnlEndService, BorderLayout.CENTER);

		// Create the panel for filter checkbox and apply button
		JPanel pnlAvailable = new JPanel(new FlowLayout());
		pnlAvailable.add(btnFilter);

		// Create a panel for all filter components. 
		JPanel pnlFilter = new JPanel();  // Panel to hold filter components
		pnlFilter.add(pnlAvailable); 

		// Create the panel for listing all rental units
		JPanel pnlListRentalUnits = new JPanel(new BorderLayout());
		pnlListRentalUnits.add(scrlPaneRentalUnits, BorderLayout.CENTER);
		pnlListRentalUnits.add(pnlAvailable, BorderLayout.EAST);

		JPanel pnlRentalUnitOps = new JPanel(new BorderLayout());
		pnlRentalUnitOps.add(pnlRemoveRestoreService, BorderLayout.SOUTH);

		// Add the panels for listing all trips and for filtering to the upper left panel
		pnlRentalUnitsLeft.add(pnlListRentalUnits, BorderLayout.CENTER);
		pnlRentalUnitsLeft.add(pnlRentalUnitOps, BorderLayout.SOUTH);

		// Right side now -- scrolling list for leases PLUS new Rental Unit
		JPanel pnlRentalUnitLeases = new JPanel(new BorderLayout());
		pnlRentalUnitLeases.add(scrlRentalUnitLeases, BorderLayout.CENTER);

		// Create the panel for new leases
		JPanel pnlTripData = new JPanel();
		pnlTripData.setLayout(new BorderLayout());
		pnlTripData.setBorder(BorderFactory.createTitledBorder(NEW_RENTAL_UNIT));

		// Create another panel for choosing the type of new rental unit
		JPanel pnlRadioNewTrip = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		ButtonGroup groupNewUnit = new ButtonGroup();
		c.gridx = 0;
		pnlRadioNewTrip.add(new JLabel(RENTAL_UNIT_KIND), c);
		for (int k = 0; k < rbtnNewKinds.length; k++) {
			c.gridx += 1;
			rbtnNewKinds[k] = new JRadioButton(rbtnKindRULabels[k + 1]);
			rbtnNewKinds[k].addActionListener(this);
			pnlRadioNewTrip.add(rbtnNewKinds[k], c);
			groupNewUnit.add(rbtnNewKinds[k]);
		}
		rbtnNewKinds[0].setSelected(true);
		pnlTripData.add(pnlRadioNewTrip, BorderLayout.NORTH);

		// Create and populate the panel for new rental unit data
		JPanel pnlUserData = new JPanel(new SpringLayout()); // User text data panel
		pnlUserData.add(new JLabel(FLOOR));
		pnlUserData.add(txtFloor);
		pnlUserData.add(new JLabel(ROOM));
		pnlUserData.add(txtRoom);
		pnlUserData.add(new JLabel(CAPACITY));
		pnlUserData.add(txtCapacity);
		SpringUtilities.makeCompactGrid(pnlUserData, 1, 6, 6, 0, 0, 2);
		pnlTripData.add(pnlUserData, BorderLayout.CENTER);       
		pnlTripData.add(btnAddRentalUnit, BorderLayout.SOUTH);

		// Add panels for listing reservations and creating new trips to right trip panel
		pnlRentalUnitsRight.add(pnlRentalUnitLeases, BorderLayout.CENTER);
		pnlRentalUnitsRight.add(pnlTripData, BorderLayout.SOUTH);  
	}

	/**
	 * Creates the client panels and put them together.
	 */
	private void setUpClientPanels() {
		// Set layout, borders, and scroll panes on the left and right side panels
		pnlClientsLeft.setLayout(new BorderLayout());
		pnlClientsRight.setLayout(new BorderLayout());
		pnlClientsLeft.setBorder(BorderFactory.createTitledBorder(ALL_CLIENTS));
		pnlClientsRight.setBorder(BorderFactory.createTitledBorder(CLIENT_LEASES));
		pnlClientsLeft.add(scrlClient, BorderLayout.CENTER);

		// Create the panel and widgets for adding a new client
		JPanel pnlAddNewClient = new JPanel(new BorderLayout());
		pnlAddNewClient.setBorder(BorderFactory.createTitledBorder(ADD_CLIENT));
		JPanel pnlNewClientData = new JPanel(new SpringLayout());
		pnlNewClientData.add(new JLabel("Client Name: "));
		pnlNewClientData.add(txtClientName);
		pnlNewClientData.add(new JLabel("Client Id: "));
		pnlNewClientData.add(txtClientContact);
		SpringUtilities.makeCompactGrid(pnlNewClientData, 2, 2, 6, 0, 6, 2);
		pnlAddNewClient.add(pnlNewClientData, BorderLayout.CENTER);
		pnlAddNewClient.add(btnAddClient, BorderLayout.SOUTH);
		pnlClientsLeft.add(pnlAddNewClient, BorderLayout.SOUTH);

		// Create the new panels and widgets for adding/canceling leases
		JPanel pnlShowReservations = new JPanel(new BorderLayout());
		pnlShowReservations.add(scrlClientLeases, BorderLayout.CENTER);
		pnlShowReservations.add(btnCancelLease, BorderLayout.SOUTH);   
		pnlClientsRight.add(pnlShowReservations, BorderLayout.CENTER);
		// Add new lease
		JPanel pnlAddLease = new JPanel(new BorderLayout());
		pnlAddLease.setBorder(BorderFactory.createTitledBorder(NEW_LEASE));
		pnlAddLease.add(btnCreateLease, BorderLayout.SOUTH);


		JPanel pnlNewLeaseInfo = new JPanel(new SpringLayout());
		pnlNewLeaseInfo.add(btnStartDatePick);
		pnlNewLeaseInfo.add(txtStartDate);
		txtStartDate.setEditable(false);
		pnlNewLeaseInfo.add(new JLabel(DURATION));
		pnlNewLeaseInfo.add(txtDuration);
		pnlNewLeaseInfo.add(new JLabel(NUM_OCCUPANTS));
		pnlNewLeaseInfo.add(txtPartySize);
		SpringUtilities.makeCompactGrid(pnlNewLeaseInfo, 3, 2, 6, 0, 6, 2);
		pnlAddLease.add(pnlNewLeaseInfo, BorderLayout.CENTER);
		pnlClientsRight.add(pnlAddLease, BorderLayout.SOUTH);
	}	

	/** 
	 * Puts all panels together in new GridLayout.
	 * @param cMain  Container to hold the panels
	 */
	private void putTogetherPanels(Container cMain) {
		cMain.setLayout(new GridLayout(2, 2));
		cMain.add(pnlRentalUnitsLeft);
		cMain.add(pnlRentalUnitsRight);
		cMain.add(pnlClientsLeft);
		cMain.add(pnlClientsRight);   
	}

	/**
	 * Performs the task corresponding to the user's action.
	 * @param  e The event that occurred (the user's action)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == itemNewFile) {
				doNewFile();
			} else if (e.getSource() == itemLoadFile) {
				doLoadFile();
			} else if (e.getSource() == itemSaveFile) {
				doSaveFile();
			} else if (e.getSource() == itemQuit) {
				doExit();
			}
			if (e.getSource().equals(btnCloseUnit))
				doCloseUnit();
			if (e.getSource().equals(btnReturnToService))  
				doReturnToService();
			if (e.getSource().equals(btnFilter)) { 
				doFiltering();
			}
			if (e.getSource().equals(btnRemoveFromService))
				doRemoveFromService();
			if (e.getSource().equals(btnAddClient)) {
				doAddClient(); 
			}
			if (e.getSource().equals(btnAddRentalUnit)) 
				doAddRentalUnit();
			if (e.getSource().equals(btnCancelLease)) { 
				doCancelLease();
			}
			if (e.getSource().equals(btnCreateLease)) 
				doCreateLease(); 
		} catch (IllegalArgumentException exc) {
			popupError(exc.getMessage(), "Data Error");
		}
	}
	
	/**
	 * Filters rental unit listing.
	 */
	private void doFiltering() {
		new FilterDialog(this);
		mgr.filterRentalUnits(displaySpecificKindOnly, displayAvailableOnly);
		this.refreshAllRentalUnits();
	}
	
	/**
	 * Closes a rental unit.
	 */
	private void doCloseUnit() {
		int index = lstRentalUnits.getSelectedIndex(); // index of unit to close
		if (index < 0) 
			throw new IllegalArgumentException(
					"Must select a rental unit to close.");
		else {
			mgr.closeRentalUnit(index);
			this.refreshAllRentalUnits();
			this.refreshLeasesForSelectedClient();
		}
	}

	/**
	 * Returns the selected rental unit to service.
	 */
	private void doReturnToService() {
		int index = lstRentalUnits.getSelectedIndex();
		if (index < 0) {
			throw new IllegalArgumentException(
					"Must select a rental unit to return to service.");
		}
		else {
			mgr.returnToService(index);
			this.refreshAllRentalUnits();
		}
	}

	/**
	 * Removes the selected rental unit from service and modifies leases accordingly.
	 */
	private void doRemoveFromService() {
		int index = lstRentalUnits.getSelectedIndex();
		if (index < 0) 
			throw new IllegalArgumentException(
					"Must select a rental unit to remove from service.");
		LocalDate start = null;
		try {
			start = dateFrom(txtNoServiceDate.getText());
		} catch (Exception e) {
			throw new IllegalArgumentException("Select a date to stop the service.");
		}
		mgr.removeFromService(index, start);
		txtNoServiceDate.setText("");
		this.refreshAllRentalUnits();
		this.refreshLeasesForSelectedClient();
	}
	
	/*
	 * Adds a new client
	 */
	private void doAddClient() {
		String name = txtClientName.getText();
		String id = txtClientContact.getText();
		try {
			mgr.addNewClient(name, id);
			txtClientName.setText("");
			txtClientContact.setText("");
			this.refreshAllClients();
		} catch (DuplicateClientException e) {
			popupError("There is already a client with an id of " + id + ".",
					"Duplicate ID Error");
		}
	}

	/** 
	 * Adds a new rental unit
	 */
	private void doAddRentalUnit() {
		int floor = 0, room = 0, capacity = 0;
		String kind = "O";
		try {
			floor =  Integer.parseInt(txtFloor.getText().trim());
			room =  Integer.parseInt(txtRoom.getText().trim());
			capacity =  Integer.parseInt(txtCapacity.getText().trim());
		} catch (Exception e) {
			popupError("Floor, room, and capacity must be whole numbers",
					"Improper Values Error");
			return;
		}
		try {
			// Here you'll change the kind fron "O" if "H" or "C" are chosen.
			if (rbtnNewKinds[1].isSelected())
				kind = rbtnKindRULabels[2].substring(0, 1); 
			else if (rbtnNewKinds[2].isSelected())
				kind = rbtnKindRULabels[3].substring(0, 1); 
			mgr.addNewUnit(kind,  floor + "-" + room, capacity);
			txtFloor.setText("");
			txtRoom.setText("");
			txtCapacity.setText("");
			this.refreshAllRentalUnits();
		} catch (DuplicateRoomException e) {	
			popupError("Rental unit at " + floor + "-" + room + " already exists.",
					"Duplicate Rental Unit Error");
		}
	}

	/**
	 * Cancels a client's lease.
	 */
	private void doCancelLease() {
		int leaseIndex = lstClientLeases.getSelectedIndex();
		int clientIndex = lstClients.getSelectedIndex();
		if (clientIndex >= 0 && leaseIndex >= 0) {
			mgr.cancelClientsLease(clientIndex, leaseIndex);
			this.refreshLeasesForSelectedClient();
			this.refreshLeasesForSelectedRentalUnit();
		}
		else 
			popupError("Must pick a lease to cancel on the client's list.",
					"Lease Cancel Error");
	}

	/**
	 * Creates a new lease.
	 */
	private void doCreateLease() {
		LocalDate start = null;
		int duration = 0;
		int occupants = 0;
		int clientPsn = lstClients.getSelectedIndex();
		int rentalUnitPsn = lstRentalUnits.getSelectedIndex();
		if (clientPsn < 0 || rentalUnitPsn < 0)
			throw new IllegalArgumentException("Select a client and rental unit for the lease.");
		try {
			duration = Integer.parseInt(txtDuration.getText());
			occupants = Integer.parseInt(txtPartySize.getText());
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Duration and number of occupants must be positive integers.");		
		}
		try {
			start = dateFrom(txtStartDate.getText());
		} catch (Exception e) {
			throw new IllegalArgumentException("Select a start date for the lease.");
		}
		try {
			mgr.createLease(clientPsn, rentalUnitPsn, start, duration, occupants);
			txtStartDate.setText("");
			txtDuration.setText("");
			txtPartySize.setText("");
			refreshLeasesForSelectedClient();
			refreshLeasesForSelectedRentalUnit();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Creates a LocalDate from a string
	 * @param s the string (in the form yyyy-mm-dd)
	 * @return LocalDate.of(yyyy,m,dd)
	 * @throws IllegalArgumentException if s is not the correct form to define a date
	 */
	private LocalDate dateFrom(String s) {
		try {
			String[] vals = s.trim().split("-");
			int year = Integer.parseInt(vals[0]);
			int month = Integer.parseInt(vals[1]);
			int day = Integer.parseInt(vals[2]);
			return LocalDate.of(year, month, day);
		} catch (Exception e) {
			throw new IllegalArgumentException("Must have a valid date.");
		}		
	}


	/**
	 * Loads rental data a from file.
	 */
	private void doLoadFile() {
		saveFirst();
		clearAllDataDisplay();
		try {
			JFileChooser chooser = new JFileChooser("./");
			FileNameExtensionFilter filterExt = new FileNameExtensionFilter("Rental data files (md)", "md");
			chooser.setFileFilter(filterExt);
			chooser.setMultiSelectionEnabled(false);
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String pick = chooser.getSelectedFile().getAbsolutePath();
				RentalReader.readRentalData(pick);
				mostRecentFileName = pick;
			}
			mgr.filterRentalUnits("All", false);
			refreshAllRentalUnits();
			refreshAllClients();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error opening file.", "Opening Error", JOptionPane.ERROR_MESSAGE);
		}
	}
		
	/**
	 * Saves rental data to a file.
	 */
	private void doSaveFile() {
		try {
			JFileChooser chooser = new JFileChooser("./");
			FileNameExtensionFilter filterExt = new FileNameExtensionFilter("Rental data files (md)", "md");
			chooser.setFileFilter(filterExt);
			chooser.setMultiSelectionEnabled(false);
			if (mostRecentFileName != null)
				chooser.setSelectedFile(new File(mostRecentFileName));
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if (!chooser.getSelectedFile().getName().endsWith(".md")) {
					throw new IllegalArgumentException();
				}
				mostRecentFileName = chooser.getSelectedFile().getAbsolutePath();
				RentalWriter.writeRentalFile(mostRecentFileName);
			}
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "File not saved.", "Saving Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doNewFile() {
		saveFirst();
		mgr.flushAllData();
		clearAllDataDisplay();
		mostRecentFileName = null;
	}

	/** Pops up a dialog with an error message 
	 * @message Error message displayed
	 * @title  Title of the dialog
	 */
	private void popupError(String message, String title) {
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	private void saveFirst() {
		if (mgr.listClients().length > 0 || mgr.listRentalUnits().length > 0) {
            int result = JOptionPane.showConfirmDialog(this, "Do you want to save the current data?", 
            		"Save Data?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION)
            	doSaveFile();
		}
	}


	/** 
	 * Adds listeners to buttons and lists of trips and clients.
	 */
	private void addListeners() {
		// Add button listeners
		btnAddRentalUnit.addActionListener(this);
		btnAddClient.addActionListener(this);
		btnCancelLease.addActionListener(this);
		btnCloseUnit.addActionListener(this);
		btnRemoveFromService.addActionListener(this);
		btnCreateLease.addActionListener(this);
		btnFilter.addActionListener(this);
		btnReturnToService.addActionListener(this);

		// Add listeners for list of all rental units and list of all clients
		lstRentalUnits.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					refreshLeasesForSelectedRentalUnit();
				}
			}
		});
		lstClients.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					refreshLeasesForSelectedClient();
				}
			}
		}); 

		// Add listeners to calendar picker buttons
		btnStartDatePick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				txtStartDate.setText(new CalendarPicker(calFrame).setPickedDate());
			}
		});
		btnServiceEndDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				txtNoServiceDate.setText(new CalendarPicker(calFrame).setPickedDate());
			}
		});
	}

	/**
	 * Refreshes the rental unit display, clears the selection and the corresponding unit leases.
	 */
	private void refreshAllRentalUnits() {
		lstRentalUnits.clearSelection();
		dlmRentalUnits.clear();
		String[] theUnits = mgr.listRentalUnits();
		for (String s: theUnits)
			dlmRentalUnits.addElement(s);
		// Now clear the associated rental unit display
		lstRentalUnitLeases.clearSelection();
		dlmRentalUnitLeases.clear();
	}

	/**
	 * Refreshes list of leases for the selected rental unit. Changes lease display.
	 */
	private void refreshLeasesForSelectedRentalUnit() {
		dlmRentalUnitLeases.clear();
		int whichUnit = lstRentalUnits.getSelectedIndex();
		if (whichUnit >= 0) {
			String[] rentalLeases = mgr.listLeasesForRentalUnit(whichUnit);
			for (String c : rentalLeases) {
				dlmRentalUnitLeases.addElement(c);
			}
		}
	}
	
	/**
	 * Refreshes the list of all clients. Changes client and associated reservation display.
	 */
	private void refreshAllClients() {
		lstClients.clearSelection();
		dlmClients.clear();
		String[] theClients = mgr.listClients();
		for (String s : theClients)
			dlmClients.addElement(s);
		// Now clear the associated client reservation display
		lstClientLeases.clearSelection();
		dlmClientLeases.clear();
	}

	/**
	 * Refreshes the list of leases for the selected client. Changes lease display.
	 */
	private void refreshLeasesForSelectedClient() {
		dlmClientLeases.clear();
		int whichClient = lstClients.getSelectedIndex();
		if (whichClient >= 0) {
			String[] clientRes = mgr.listClientLeases(whichClient);
			for (String c : clientRes) {
				dlmClientLeases.addElement(c);
			} 
		}
	}
	
	/** 
	 * Clears all data from the GUI.
	 */
	private void clearAllDataDisplay() {
		mgr.filterRentalUnits("All", false);
		txtCapacity.setText("");
		txtClientContact.setText("");
		txtClientName.setText("");
		txtDuration.setText("");
		txtFloor.setText("");
		txtNoServiceDate.setText("");
		txtPartySize.setText("");
		txtRoom.setText("");
		txtStartDate.setText("");
		lstClients.clearSelection();
		dlmClients.clear();
		lstClientLeases.clearSelection();
		dlmClientLeases.clear();
		lstRentalUnits.clearSelection();
		dlmRentalUnits.clear();
		lstRentalUnitLeases.clearSelection();
		dlmRentalUnitLeases.clear();
	}


	/**
	 * Exits the GUI. First queries user on whether to save data if needed.
	 */
	private void doExit() {
		saveFirst();
		System.exit(NORMAL);
	}
	
	/**
	 * Custom inner dialog class for applying filters.
	 * @author Jo Perry
	 *
	 */
	private class FilterDialog extends JDialog implements ActionListener {
		private static final long serialVersionUID = 1L; 
		/** Apply filter button */
		private JButton btnApply = new JButton("Apply Filters");
		/** Button to cancel changing filters */
		private JButton btnCancel = new JButton("Cancel");
		/** Main panel to hold the two types of filters */
		private JPanel pnlMain = new JPanel();
		/** Panel to hold the widgets for the "Available Only" filter */
		private JPanel pnlAvailable = new JPanel(new FlowLayout());
		/** Panel to finish filter selection */
		private JPanel pnlQuit = new JPanel(new FlowLayout());
		
		/** Filter checkbox -- user can check to show only available rental units */
		private JCheckBox cbxAvailable = new JCheckBox("Available Rental Units Only");
		/** Radio buttons for filtering the rental unit display by kind */
		private JRadioButton[] rbtnKinds = new JRadioButton[4];

		/** 
		 * Constructor creates the filter dialog.
		 * @param parent parent frame (parent class)
		 */
		public FilterDialog(Frame parent) {
			super(parent, "Filter Rental Unit Display", true);
			JPanel pnlRadioFilter = new JPanel(new GridBagLayout()); 
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			pnlRadioFilter.add(new JLabel(RENTAL_UNIT_KIND), c);
			ButtonGroup group = new ButtonGroup();
			for (int k = 0; k < rbtnKinds.length; k++) {
				rbtnKinds[k] = new JRadioButton(rbtnKindRULabels[k]);
				group.add(rbtnKinds[k]);
				c.gridx += 1;  
				pnlRadioFilter.add(rbtnKinds[k], c);
			}
			rbtnKinds[0].setSelected(true);
			pnlAvailable.add(cbxAvailable);
			pnlQuit.add(btnCancel);
			btnCancel.addActionListener(this);
			btnApply.addActionListener(this);
			pnlQuit.add(btnApply);
			pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));
			pnlMain.add(pnlRadioFilter);
			pnlMain.add(pnlAvailable);
			pnlMain.add(pnlQuit);
			getContentPane().add(pnlMain);
			pack();
			this.setLocation(200, 200);
			this.setVisible(true);
		}
		
		/**
		 * Applies the filters and closes the dialog when the "Apply" button is clicked.
		 */
		public void actionPerformed(ActionEvent ae) {
			if (ae.getSource() == btnApply) {
				displaySpecificKindOnly = "A";
				for (int k = 1; k < 4; k++)
					if (rbtnKinds[k].isSelected()) 
						displaySpecificKindOnly = rbtnKindRULabels[k];
				displayAvailableOnly = cbxAvailable.isSelected();
			}
			dispose();
		}
	}

}