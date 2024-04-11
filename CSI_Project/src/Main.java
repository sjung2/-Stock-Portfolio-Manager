import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Asset {
    private double value;

    public Asset(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

class Stock extends Asset {
    private String tickerSymbol;
    private double priceBought;
    private int quantity;
    private Date dateBought;

    public Stock(String tickerSymbol, double priceBought, int quantity, Date dateBought) {
        super(priceBought * quantity); // Calculate value based on price bought and quantity
        this.tickerSymbol = tickerSymbol;
        this.priceBought = priceBought;
        this.quantity = quantity;
        this.dateBought = dateBought;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public double getPriceBought() {
        return priceBought;
    }

    public void setPriceBought(double priceBought) {
        this.priceBought = priceBought;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDateBought() {
        return dateBought;
    }

    @Override
    public String toString() {
        return tickerSymbol; // Return ticker symbol for JComboBox display
    }
}

class Portfolio {
    private List<Stock> stocks;

    public Portfolio() {
        this.stocks = new ArrayList<>();
    }

    public void addStock(Stock stock) {
        stocks.add(stock);
    }

    public void removeStock(String tickerSymbol) {
        removeStockRecursively(tickerSymbol, 0);
    }

    private void removeStockRecursively(String tickerSymbol, int index) {
        if (index >= stocks.size()) {
            return; // Base case: stock not found
        }

        if (stocks.get(index).getTickerSymbol().equals(tickerSymbol)) {
            stocks.remove(index); // Found the stock, remove it
        } else {
            removeStockRecursively(tickerSymbol, index + 1); // Recur to next index
        }
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public double getTotalValue() {
        double totalValue = 0;
        for (Stock stock : stocks) {
            totalValue += (stock.getPriceBought() * stock.getQuantity());
        }
        return totalValue;
    }
}

class PortfolioTrackerGUI extends JFrame {
    private JTextField tickerSymbolField, priceField, quantityField, dateField;
    private JComboBox<Stock> stockDropdown;
    private JTextArea portfolioArea;
    private Portfolio portfolio;
    private JButton totalButton;

    public PortfolioTrackerGUI() {
        setTitle("Portfolio Tracker");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Apply dark look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setBackground(Color.darkGray);

        JPanel addPanel = new JPanel(new GridLayout(5, 2));
        addPanel.setBackground(Color.darkGray);
        addPanel.add(new JLabel("Ticker Symbol:"));
        tickerSymbolField = new JTextField();
        addPanel.add(tickerSymbolField);
        addPanel.add(new JLabel("Price Bought:"));
        priceField = new JTextField();
        addPanel.add(priceField);
        addPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        addPanel.add(quantityField);
        addPanel.add(new JLabel("Date Bought (MM/dd/yyyy):"));
        dateField = new JTextField();
        addPanel.add(dateField);

        JButton addButton = new JButton("Add Stock");
        addButton.setBackground(Color.blue);
        addButton.setForeground(Color.white);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStockToPortfolio();
            }
        });
        addPanel.add(addButton);
        add(addPanel, BorderLayout.NORTH);

        JPanel updatePanel = new JPanel(new GridLayout(1, 2));
        JButton updateButton = new JButton("Update Stocks");
        updateButton.setBackground(Color.blue);
        updateButton.setForeground(Color.white);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUpdateDialog();
            }
        });
        updatePanel.add(updateButton);
        add(updatePanel, BorderLayout.CENTER);

        stockDropdown = new JComboBox<>();
        JPanel removePanel = new JPanel(new BorderLayout());
        removePanel.setBackground(Color.darkGray);
        removePanel.add(new JLabel("Select Stock to Remove:"), BorderLayout.WEST);
        removePanel.add(stockDropdown, BorderLayout.CENTER);
        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(Color.blue);
        removeButton.setForeground(Color.white);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStockFromPortfolio();
            }
        });
        removePanel.add(removeButton, BorderLayout.EAST);
        add(removePanel, BorderLayout.SOUTH);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.darkGray);
        totalButton = new JButton("Generate Total Assets");
        totalButton.setBackground(Color.blue);
        totalButton.setForeground(Color.white);
        totalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTotalAssetsDialog();
            }
        });
        totalPanel.add(totalButton, BorderLayout.CENTER);
        add(totalPanel, BorderLayout.WEST);

        portfolioArea = new JTextArea();
        portfolioArea.setEditable(false);
        portfolioArea.setBackground(Color.darkGray);
        portfolioArea.setForeground(Color.white);
        JScrollPane scrollPane = new JScrollPane(portfolioArea);
        add(scrollPane, BorderLayout.EAST);

        portfolio = new Portfolio();
    }

    private void addStockToPortfolio() {
        String tickerSymbol = tickerSymbolField.getText();
        double priceBought = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        Date dateBought = null;
        try {
            dateBought = new SimpleDateFormat("MM/dd/yyyy").parse(dateField.getText());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Stock stock = new Stock(tickerSymbol, priceBought, quantity, dateBought);
        portfolio.addStock(stock);
        updateStockDropdown();
        updatePortfolioDisplay();
        clearInputFields();
        updateTotalAssets();
    }

    private void openUpdateDialog() {
        // Create dialog
        JDialog dialog = new JDialog(this, "Update Stock", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(3, 2));

        // Add components to dialog
        dialog.add(new JLabel("Select Stock:"));
        JComboBox<Stock> stockDropdown = new JComboBox<>();
        for (Stock stock : portfolio.getStocks()) {
            stockDropdown.addItem(stock);
        }
        dialog.add(stockDropdown);

        dialog.add(new JLabel("New Quantity:"));
        JTextField newQuantityField = new JTextField();
        dialog.add(newQuantityField);

        dialog.add(new JLabel("New Price:"));
        JTextField newPriceField = new JTextField();
        dialog.add(newPriceField);

        // Add save button
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(Color.blue);
        saveButton.setForeground(Color.white);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stock selectedStock = (Stock) stockDropdown.getSelectedItem();
                if (selectedStock != null) {
                    String newQuantityText = newQuantityField.getText();
                    String newPriceText = newPriceField.getText();
                    if (!newQuantityText.isEmpty()) {
                        int newQuantity = Integer.parseInt(newQuantityText);
                        selectedStock.setQuantity(newQuantity);
                    }
                    if (!newPriceText.isEmpty()) {
                        double newPrice = Double.parseDouble(newPriceText);
                        selectedStock.setPriceBought(newPrice);
                    }
                    dialog.dispose(); // Close the dialog
                    updatePortfolioDisplay(); // Update main window display
                    updateTotalAssets(); // Update total assets
                }
            }
        });
        dialog.add(saveButton);

        // Set dialog visibility
        dialog.setVisible(true);
    }

    private void removeStockFromPortfolio() {
        Stock selectedStock = (Stock) stockDropdown.getSelectedItem();
        if (selectedStock != null) {
            String tickerSymbol = selectedStock.getTickerSymbol();
            portfolio.removeStock(tickerSymbol);
            updateStockDropdown();
            updatePortfolioDisplay();
            updateTotalAssets();
        }
    }

    private void updateStockDropdown() {
        stockDropdown.removeAllItems();
        for (Stock stock : portfolio.getStocks()) {
            stockDropdown.addItem(stock);
        }
    }

    private void clearInputFields() {
        tickerSymbolField.setText("");
        priceField.setText("");
        quantityField.setText("");
        dateField.setText("");
    }

    private void updatePortfolioDisplay() {
        StringBuilder sb = new StringBuilder();
        for (Stock stock : portfolio.getStocks()) {
            sb.append("Ticker: ").append(stock.getTickerSymbol()).append(", ")
                    .append("Price Bought: ").append(stock.getPriceBought()).append(", ")
                    .append("Quantity: ").append(stock.getQuantity()).append(", ")
                    .append("Date Bought: ").append(stock.getDateBought()).append("\n");
        }
        portfolioArea.setText(sb.toString());
    }

    private void showTotalAssetsDialog() {
        double totalAssets = portfolio.getTotalValue();
        JOptionPane.showMessageDialog(this, "Total Assets: $" + totalAssets, "Total Assets", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTotalAssets() {
        double totalAssets = portfolio.getTotalValue();
        totalButton.setText("Generate Total Assets: $" + totalAssets);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PortfolioTrackerGUI().setVisible(true);
            }
        });
    }
}