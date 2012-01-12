package com.example.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jmock.example.announcer.Announcer;

import com.example.auctionsniper.Item;
import com.example.auctionsniper.SniperPortfolio;
import com.example.auctionsniper.UserRequestListener;

public class MainWindow extends JFrame {

	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String APPLICATION_TITLE = "Auction Sniper";
	public static final String NEW_ITEM_ID_NAME = "item id";
	public static final String JOIN_BUTTON_NAME = "join button";
	public static final String STOP_PRICE_NAME = "stop price";

	private static final String SNIPERS_TABLE_NAME = "Snipers";
	private static final long serialVersionUID = 1L;

	private final Announcer<UserRequestListener> userRequests = Announcer.to(UserRequestListener.class);
	private JFormattedTextField stopPriceField;
	private JTextField itemIdField;

	public MainWindow(final SniperPortfolio portfolio) {
		super("Auction Sniper");
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable(portfolio), makeControls());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private JPanel makeControls() {
		final JPanel controls = new JPanel(new FlowLayout());

		itemIdField = new JTextField();
		itemIdField.setColumns(25);
		itemIdField.setName(NEW_ITEM_ID_NAME);
		controls.add(itemIdField);

		stopPriceField = new JFormattedTextField();
		stopPriceField.setColumns(25);
		stopPriceField.setName(STOP_PRICE_NAME);
		stopPriceField.setFormatterFactory(new AbstractFormatterFactory() {
			@Override
			public AbstractFormatter getFormatter(final JFormattedTextField tf) {
				return new AbstractFormatter() {
					private static final long serialVersionUID = 1L;

					@Override
					public String valueToString(final Object value) throws ParseException {
						return String.valueOf(value);
					}

					@Override
					public Object stringToValue(final String text) throws ParseException {
						return Integer.parseInt(text);
					}
				};
			}
		});
		controls.add(stopPriceField);

		final JButton joinAuctionButton = new JButton("Join auction");
		joinAuctionButton.setName(JOIN_BUTTON_NAME);
		joinAuctionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent event) {
				userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
			}

			private int stopPrice() {
				return ((Number) stopPriceField.getValue()).intValue();
			}

			private String itemId() {
				return itemIdField.getText();
			}
		});
		controls.add(joinAuctionButton);

		return controls;
	}

	private void fillContentPane(final JTable snipersTable, final JPanel controls) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(controls, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}

	private JTable makeSnipersTable(final SniperPortfolio portfolio) {
		final SnipersTableModel model = new SnipersTableModel();
		portfolio.addPortfolioListener(model);
		final JTable snipersTable = new JTable(model);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	public void addUserRequestListener(final UserRequestListener userRequestListener) {
		userRequests.addListener(userRequestListener);
	}
}
