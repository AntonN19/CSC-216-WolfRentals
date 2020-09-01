package edu.ncsu.csc216.business.view.ui.utils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

/**
 * This class definition was modified from the example published on this page: 
 *  https://blog.eduonix.com/java-programming-2/how-to-use-date-picker-component-in-java/
 *  
 *  @author Jo Perry
 */
public class CalendarPicker 
{
	/** Month as an integer, initialized to current month */	
	private int month = Calendar.getInstance().get(Calendar.MONTH);
	/** Year as an integer, initialized to current year */
	private int year = Calendar.getInstance().get(Calendar.YEAR);
	/** Label for the calendar at the bottom. Changes going forward/backward through the calendar. */
	private JLabel lblYearMonth = new JLabel("", JLabel.CENTER);
	/** Selected date as a string */
	private String day = "";
	/** Dialog to open when the date picker button is clicked */
	private JDialog dlgCalendar = new JDialog();
	/** Buttons shown on the calendar. Top row is for days of week header */
	private JButton[] btnCalCell = new JButton[49];
	/** Days of the week. */
	private static final String[] HEADER = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
	/** Panel to display days of the month */
	private JPanel pnlCalDays = new JPanel(new GridLayout(7, 7));
	/** Panel to display month, year and buttons to go forward/back a month */
	private JPanel pnlCalMonthControls = new JPanel(new GridLayout(1, 3)); 
	/** Button for the previous month */
	private JButton btnPrevious = new JButton("<< Previous"); 
	/** Button for the next month */
	private JButton btnNext = new JButton("Next >>"); 
	
	/**
	 * Constructor creates a new dialog displaying the calendar.
	 * @param parent Creator of the new CalendarPicker object
	 */
	public CalendarPicker(JFrame parent)//create constructor 
	{
		dlgCalendar.setModal(true);
		pnlCalDays.setPreferredSize(new Dimension(430, 120));
		// Fill the dialog with buttons, one for each day
		for (int x = 0; x < btnCalCell.length; x++) {		
			final int selection = x;   // Day button clicked by the user
			btnCalCell[x] = new JButton();
			btnCalCell[x].setFocusPainted(false);
			btnCalCell[x].setBackground(Color.white);
			if (x > 6)  // Now set up action listeners for days of the month
				btnCalCell[x].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
						day = btnCalCell[selection].getActionCommand();
						dlgCalendar.dispose();
					}
				});
			if (x < 7) { // This is the header (with the days of the week). Set them with red text. 
				btnCalCell[x].setText(HEADER[x]);
				btnCalCell[x].setForeground(Color.red);
			}
			// Now add the button to the calendar panel
			pnlCalDays.add(btnCalCell[x]); 
		}
		// Set the Previous button to work
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				month--;
				displayDate();
			}
		});
		pnlCalMonthControls.add(btnPrevious); 
		pnlCalMonthControls.add(lblYearMonth); 
		// And set the Next button to work
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				month++;
				displayDate();
			}
		});
		pnlCalMonthControls.add(btnNext); 
		// Set border alignment, location
		dlgCalendar.add(pnlCalDays, BorderLayout.CENTER);
		dlgCalendar.add(pnlCalMonthControls, BorderLayout.SOUTH);
		dlgCalendar.pack();
		dlgCalendar.setLocationRelativeTo(parent);
		// Finally, display it all
		displayDate();
		dlgCalendar.setVisible(true);
	}

	/**
	 * Set up the calendar to display the month & year
	 */
	public void displayDate() {
		// First clear off text from buttons for any previous/next month's days
		for (int z = 7; z < btnCalCell.length; z++) {
			btnCalCell[z].setText(""); 
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");	// Format for the month year display
		Calendar cal = GregorianCalendar.getInstance();	 // Empty calendar
		// Set the calendar to begin on the first day of the month & year
		cal.set(year, month, 1); 
		int dayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);
		int daysInMonth = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		// Set the text on the buttons to correspond to numbered days
		for (int x = 6 + dayOfWeek, theDay = 1; theDay <= daysInMonth; x++, theDay++) {
			btnCalCell[x].setText("" + theDay);
		}
		lblYearMonth.setText(sdf.format(cal.getTime()));
		dlgCalendar.repaint();
	}

	/**
	 * What is the date the user picked?
	 * @return the user's choice in the form "yyyy-MM-dd", where MM numbering
	 *         starts at 01. If the user didn't make a 
	 *         selection or clicked a blank button, return the empty string.
	 */
	public String setPickedDate() {
		// If user closed the dialog directly or clicked a blank day button, return ""
		if (day.equals(""))  
			return day;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Format for returned date
		Calendar cal = GregorianCalendar.getInstance();  // Calendar (needed for formatting)
		cal.set(year, month, Integer.parseInt(day));
		return sdf.format(cal.getTime());
	}

}