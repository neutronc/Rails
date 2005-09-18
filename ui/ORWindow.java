/*
 * Created on Apr 29, 2005
 */
package ui;

import game.*;
import ui.elements.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author blentz
 */
public class ORWindow extends JFrame implements ActionListener
{
   private static final int NARROW_GAP = 1;
   private static final int WIDE_GAP = 3;
   private static final int WIDE_LEFT = 1;
   private static final int WIDE_RIGHT = 2;
   private static final int WIDE_TOP = 4;
   private static final int WIDE_BOTTOM = 8;
   
   private static ORWindow orPanel;
   private JPanel statusPanel;
   private JPanel buttonPanel;

   private GridBagLayout gb;
   private GridBagConstraints gbc;
   
   // Grid elements per function
   private Caption leftCompName[];
   private int leftCompNameXOffset, leftCompNameYOffset;
   private Caption rightCompName[];
   private int rightCompNameXOffset, rightCompNameYOffset;
   private Field president[];
   private int presidentXOffset, presidentYOffset;
   private Field sharePrice[];
   private int sharePriceXOffset, sharePriceYOffset;
   private Field cash[];
   private int cashXOffset, cashYOffset;
   private Field trains[];
   private int trainsXOffset, trainsYOffset;
   private Field tiles[];
   private int tilesXOffset, tilesYOffset;
   private Field tileCost[];
   private Select tileCostSelect[];
   private Field tokens[];
   private Field tokenCost[];
   private Select tokenCostSelect[];
   private int tokensXOffset, tokensYOffset;
   private Field revenue[];
   private Spinner revenueSelect[];
   private Field decision[];
   private int revXOffset, revYOffset;
   private Field newTrains[];
   private Field newTrainCost[];
   private Select newTrainCostSelect[]; 
   private int newTrainsXOffset, newTrainsYOffset;
  
   private Caption[] upperPlayerCaption;
   private Caption[] lowerPlayerCaption;
   
   private JButton leftButton;
   private JButton middleButton;
   private JButton rightButton;
   private JButton closeButton;

   private int np; // Number of players
   private int nc; // Number of companies
   private Player[] players;
   private PublicCompanyI[] companies;
   private OperatingRound round;
   private StatusWindow statusWindow;
   private GameStatus gameStatus;
    
   private Player p;
   private PublicCompanyI c;
   private JComponent f;
   
   // Current state
   private int playerIndex = -1;
   private int orCompIndex = -1;
   private PublicCompanyI orComp = null;
   private String orCompName = "";
   
   //private ButtonGroup itemGroup = new ButtonGroup();
   //private ClickField dummyButton; // To be selected if none else is.
   
   private Pattern buyTrainPattern = Pattern.compile("(.+)-train from (\\S+).*");
   private int[] newTrainTotalCost;
   List trainsBought;
   
   
   public ORWindow (OperatingRound round, StatusWindow parent)
   {
   	  super ();
   	  this.round = round;
   	  statusWindow = parent;
   	  gameStatus = parent.getGameStatus();
   	  orPanel = this;
 	  getContentPane().setLayout(new BorderLayout());

 	  statusPanel = new JPanel();
 	  gb = new GridBagLayout();
 	  statusPanel.setLayout(gb);
      statusPanel.setBorder(BorderFactory.createEtchedBorder());
      statusPanel.setOpaque(true);
   	  
   	  buttonPanel = new JPanel();
   	  
   	  leftButton = new JButton("Lay tiles");
   	  leftButton.setActionCommand("LayTile");
   	  leftButton.addActionListener(this);
   	  leftButton.setEnabled(true);
   	  buttonPanel.add(leftButton);
   	  
   	  middleButton = new JButton("Buy private");
   	  middleButton.setActionCommand ("BuyPrivate");
   	  middleButton.addActionListener(this);
   	  middleButton.setEnabled(false);
   	  buttonPanel.add(middleButton);
   	  
  	  closeButton = new JButton("Close private");
   	  closeButton.setActionCommand("ClosePrivate");
   	  closeButton.addActionListener(this);
   	  closeButton.setEnabled(true);
   	  buttonPanel.add(closeButton);

  	  rightButton = new JButton("Done");
   	  rightButton.setActionCommand("Done");
   	  rightButton.addActionListener(this);
   	  rightButton.setEnabled(true);
   	  buttonPanel.add(rightButton);

   	  buttonPanel.setOpaque(true);
   	  
      gbc = new GridBagConstraints();

      players = Game.getPlayerManager().getPlayersArray();
      np = GameManager.getNumberOfPlayers();
      //companies = (PublicCompanyI[])Game.getCompanyManager().getAllPublicCompanies().toArray(new PublicCompanyI[0]);
      companies = round.getOperatingCompanies();
      nc = companies.length;
      
	  //packet = round.getStartPacket();
	  //items = (StartItem[]) packet.getItems().toArray(new StartItem[0]);
	  //ni = items.length;
      
      init();

   	  getContentPane().add(statusPanel, BorderLayout.NORTH);
   	  getContentPane().add(buttonPanel, BorderLayout.SOUTH);
   	  setTitle ("Operating Round");
      setLocation(300, 150);
      setSize(800, 400);
      pack();
      setVisible(true);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      updateStatus();
      
      LogWindow.addLog();

   }
   
   private void init () {
   	
        leftCompName = new Caption[nc];
        rightCompName = new Caption[nc];
        president = new Field[nc];
        sharePrice = new Field[nc];
	    cash = new Field[nc];
	    trains = new Field[nc];
	    tiles = new Field[nc];
	    tileCost = new Field[nc];
	    tileCostSelect = new Select[nc];
	    tokens = new Field[nc];
	    tokenCost = new Field[nc];
	    tokenCostSelect = new Select[nc];
	    revenue = new Field[nc];
	    revenueSelect = new Spinner[nc];
	    decision = new Field[nc];
	    newTrains = new Field[nc];
	    newTrainCost = new Field[nc];
	    newTrainTotalCost = new int[nc];

	    leftCompNameXOffset = 0;
	    leftCompNameYOffset = 2;
	    presidentXOffset = leftCompNameXOffset + 1;
	    presidentYOffset = leftCompNameYOffset;
	    sharePriceXOffset = presidentXOffset + 1;
	    sharePriceYOffset = leftCompNameYOffset;
        cashXOffset = sharePriceXOffset + 1;
        cashYOffset = leftCompNameYOffset;
        trainsXOffset = cashXOffset + 1;
        trainsYOffset = leftCompNameYOffset;
        tilesXOffset = trainsXOffset + 1;
        tilesYOffset = leftCompNameYOffset;
        tokensXOffset = tilesXOffset + 2;
        tokensYOffset = leftCompNameYOffset;
        revXOffset = tokensXOffset + 2;
        revYOffset = leftCompNameYOffset;
        newTrainsXOffset = revXOffset + 2;
        newTrainsYOffset = leftCompNameYOffset;
	    rightCompNameXOffset = newTrainsXOffset + 2;
	    rightCompNameYOffset = leftCompNameYOffset;
       
        addField (new Caption("Company"), 0, 0, 1, 2, WIDE_BOTTOM+WIDE_RIGHT);
        addField (new Caption("President"), presidentXOffset, 0, 1, 2, WIDE_BOTTOM);
        addField (new Caption("<html>Share<br>value</html>"), sharePriceXOffset, 0, 1, 2, 	WIDE_BOTTOM);
        addField (new Caption("Treasury"), cashXOffset, 0, 1, 2, WIDE_BOTTOM);
        addField (new Caption("Trains"), trainsXOffset, 0, 1, 2, WIDE_RIGHT+WIDE_BOTTOM);
        addField (new Caption("Tiles"), tilesXOffset, 0, 2, 1, WIDE_RIGHT);
        addField (new Caption("laid"), tilesXOffset, 1, 1, 1, WIDE_BOTTOM);
        addField (new Caption("cost"), tilesXOffset+1, 1, 1, 1, WIDE_BOTTOM+WIDE_RIGHT);
        addField (new Caption("Tokens"), tokensXOffset, 0, 2, 1, WIDE_RIGHT);
        addField (new Caption("laid"), tokensXOffset, 1, 1, 1, WIDE_BOTTOM);
        addField (new Caption("cost"), tokensXOffset+1, 1, 1, 1, WIDE_BOTTOM+WIDE_RIGHT);
        addField (new Caption("Revenue"), revXOffset, 0, 2, 1, WIDE_RIGHT);
        addField (new Caption("earned"), revXOffset, 1, 1, 1, WIDE_BOTTOM);
        addField (new Caption("payout"), revXOffset+1, 1, 1, 1, WIDE_BOTTOM+WIDE_RIGHT);
        addField (new Caption("Trains"), newTrainsXOffset, 0, 2, 1, WIDE_RIGHT);
        addField (new Caption("bought"), newTrainsXOffset, 1, 1, 1, WIDE_BOTTOM);
        addField (new Caption("cost"), newTrainsXOffset+1, 1, 1, 1, WIDE_BOTTOM+WIDE_RIGHT);
        addField (new Caption("Company"), newTrainsXOffset+2, 0, 1, 2, WIDE_BOTTOM);
        
        
        for (int i=0; i<nc; i++) {
            c = companies[i];
            f = leftCompName[i] = new Caption(c.getName());
   			f.setBackground(companies[i].getBgColour());
  			f.setForeground(companies[i].getFgColour());
  			addField (f, leftCompNameXOffset, leftCompNameYOffset+i, 1, 1, WIDE_RIGHT);
            
            f = president[i] = new Field(c.hasStarted() ? c.getPresident().getName() : "");
            addField (f, presidentXOffset, presidentYOffset+i, 1, 1, 0);
   
            f = sharePrice[i] = new Field(c.hasStockPrice() && c.hasStarted()  
                    ? Bank.format (c.getCurrentPrice().getPrice()) : "");
            addField (f, sharePriceXOffset, sharePriceYOffset+i, 1, 1, 0);
            
            f = cash[i] = new Field(Bank.format(c.getCash()));
            addField (f, cashXOffset, cashYOffset+i, 1, 1, 0);
            
            f = trains[i] = new Field("");
            addField (f, trainsXOffset, trainsYOffset+i, 1, 1, WIDE_RIGHT);
            
            f = tiles[i] = new Field("");
            addField (f, tilesXOffset, tilesYOffset+i, 1, 1, 0);
            
            f = tileCost[i] = new Field("");
            addField (f, tilesXOffset+1, tilesYOffset+i, 1, 1, WIDE_RIGHT);
            f = tileCostSelect[i] = new Select(round.getTileBuildCosts());
            tileCostSelect[i].setEditable(true);
            tileCostSelect[i].setPreferredSize(new Dimension(50,10));
            addField (f, tilesXOffset+1, tilesYOffset+i, 1, 1, WIDE_RIGHT);

            f = tokens[i] = new Field("");
            addField (f, tokensXOffset, tokensYOffset+i, 1, 1, 0);
            
            f = tokenCost[i] = new Field("");
            addField (f, tokensXOffset+1, tokensYOffset+i, 1, 1, WIDE_RIGHT);
            f = tokenCostSelect[i] = new Select(round.getTokenLayCosts());
            tokenCostSelect[i].setPreferredSize(new Dimension(50,10));
            addField (f, tokensXOffset+1, tokensYOffset+i, 1, 1, WIDE_RIGHT);

            f = revenue[i] = new Field("");
            addField (f, revXOffset, revYOffset+i, 1, 1, 0);
            f = revenueSelect[i] = new Spinner(0, 0, 0, 10);
            addField (f, revXOffset, revYOffset+i, 1, 1, 0);
            
            f = decision[i] = new Field("");
            addField (f, revXOffset+1, revYOffset+i, 1, 1, WIDE_RIGHT);

            f = newTrains[i] = new Field("");
            addField (f, newTrainsXOffset, newTrainsYOffset+i, 1, 1, 0);
            
            f = newTrainCost[i] = new Field("");
            addField (f, newTrainsXOffset+1, newTrainsYOffset+i, 1, 1, WIDE_RIGHT);
            
 
           
            f = rightCompName[i] = new Caption(c.getName());
  			f.setBackground(companies[i].getBgColour());
  			f.setForeground(companies[i].getFgColour());
            addField (f, rightCompNameXOffset, rightCompNameYOffset+i, 1, 1, 0);
            
         }
        
    }

   
   private void addField (JComponent comp, int x, int y, int width, int height,
   		int wideGapPositions) {
   	
   		int padTop,padLeft,padBottom,padRight;
       gbc.gridx = x;
       gbc.gridy = y;
       gbc.gridwidth = width;
       gbc.gridheight = height;
       gbc.weightx = gbc.weighty = 0.5;
       gbc.fill = GridBagConstraints.BOTH;
       padTop = (wideGapPositions & WIDE_TOP) > 0 ? WIDE_GAP : NARROW_GAP;
       padLeft = (wideGapPositions & WIDE_LEFT) > 0 ? WIDE_GAP : NARROW_GAP;
       padBottom = (wideGapPositions & WIDE_BOTTOM) > 0 ? WIDE_GAP : NARROW_GAP;
       padRight = (wideGapPositions & WIDE_RIGHT) > 0 ? WIDE_GAP : NARROW_GAP;
       gbc.insets = new Insets (padTop,padLeft,padBottom,padRight);

       statusPanel.add (comp, gbc);
       
   }
   
   public void updateStatus() {

       if (GameManager.getInstance().getCurrentRound() instanceof OperatingRound) {
           
           round = (OperatingRound) GameManager.getInstance().getCurrentRound();
           int step = round.getStep();
           if (round.getOperatingCompanyIndex() != orCompIndex) {
               setORCompanyTurn (round.getOperatingCompanyIndex());
           	}
           
           if (step == OperatingRound.STEP_LAY_TRACK) {
               
               tileCostSelect[orCompIndex].setSelectedIndex(0);
               setSelect (tileCost[orCompIndex], tileCostSelect[orCompIndex], true);
               
               leftButton.setText("Lay track");
               leftButton.setActionCommand("LayTrack");
               leftButton.setEnabled(true);
               
               middleButton.setText("Buy Private");
               middleButton.setActionCommand("BuyPrivate");
               middleButton.setEnabled(orComp.canBuyPrivates());
           
               rightButton.setText("Done");
          	   rightButton.setActionCommand("Done");
           	   rightButton.setEnabled(true);
           	   
           } else if (step == OperatingRound.STEP_LAY_TOKEN) {
               
               tokenCostSelect[orCompIndex].setSelectedIndex(0);
               setSelect (tokenCost[orCompIndex], tokenCostSelect[orCompIndex], true);

               leftButton.setText("Lay token");
               leftButton.setActionCommand("LayToken");
               leftButton.setEnabled(true);
               
           } else if (step == OperatingRound.STEP_CALC_REVENUE) {
               
               revenueSelect[orCompIndex].setValue(new Integer(companies[orCompIndex].getLastRevenue()));
               setSelect (revenue[orCompIndex], revenueSelect[orCompIndex], true);

               leftButton.setText("Set revenue");
               leftButton.setActionCommand("SetRevenue");
               leftButton.setEnabled(true);
               
          } else if (step == OperatingRound.STEP_PAYOUT) {
              
              leftButton.setText("Withhold");
              leftButton.setActionCommand("Withhold");
              leftButton.setEnabled(true);
              
              middleButton.setText("Split");
              middleButton.setActionCommand("Split");
              middleButton.setEnabled(companies[orCompIndex].isSplitAllowed());
              
              rightButton.setText("Pay out");
              rightButton.setActionCommand("Payout");
              rightButton.setEnabled(true);
              
          } else if (step == OperatingRound.STEP_BUY_TRAIN) {
              
              leftButton.setText("Buy train");
              leftButton.setActionCommand("BuyTrain");
              leftButton.setEnabled(true);
              
              middleButton.setText("Buy Private");
              middleButton.setActionCommand("BuyPrivate");
              middleButton.setEnabled(orComp.canBuyPrivates());
          
              rightButton.setText("Done");
         	   rightButton.setActionCommand("Done");
          	   rightButton.setEnabled(true);
          	   
         } else if (step == OperatingRound.STEP_FINAL) {
             
             leftButton.setEnabled(false);
             
         }
           
            
       } else {
            setORCompanyTurn (-1);
      		statusWindow.resume (this);
       		this.dispose();
       }
   }
   
   public void refreshStatus()
   {
      //companyStatus.refreshPanel();
      //playerStatus.refreshPanel();
      //certStatus.refreshPanel();
      //updateStatus();
      //FIXME: Not an ideal fix for various repainting issues, but it works well enough for now.
      //this.pack();
      //System.out.println("StatusWindow Dimensions: " + this.getWidth() + ", " + this.getHeight());
   }
   
   public void repaint()
   {
      super.repaint();
      //refreshStatus();
   }
   
   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent actor)
   {
       JComponent source = (JComponent)actor.getSource();
       String command = actor.getActionCommand();
       int step = round.getStep();
       boolean done = command.equals("Done");
       int amount;
       
       if (command.equals("LayTrack") || done && step == OperatingRound.STEP_LAY_TRACK) {
           amount = done ? 0 : Integer.parseInt((String)tileCostSelect[orCompIndex].getSelectedItem());
           tileCost[orCompIndex].setText (amount > 0 ? Bank.format(amount) : "");
           round.layTrack(orCompName, amount);
           setSelect (tileCost[orCompIndex], tileCostSelect[orCompIndex], false);
           updateCash(orCompIndex);
           gameStatus.updateCompany(orComp.getPublicNumber());
           gameStatus.updateBank();
           
       } else if (command.equals("LayToken") || done && step == OperatingRound.STEP_LAY_TOKEN) {
           amount = done ? 0 : Integer.parseInt((String)tokenCostSelect[orCompIndex].getSelectedItem());
           tokenCost[orCompIndex].setText (amount > 0 ? Bank.format(amount) : "");
           round.layToken(orCompName, amount);
           setSelect (tokenCost[orCompIndex], tokenCostSelect[orCompIndex], false);
           updateCash(orCompIndex);
           gameStatus.updateCompany(orComp.getPublicNumber());
           gameStatus.updateBank();
           
       } else if (command.equals("SetRevenue") || done && step == OperatingRound.STEP_CALC_REVENUE) {
           amount = done ? 0 : ((Integer)revenueSelect[orCompIndex].getValue()).intValue();
           revenue[orCompIndex].setText (Bank.format(amount));
           round.setRevenue(orCompName, amount);
           setSelect (revenue[orCompIndex], revenueSelect[orCompIndex], false);
           gameStatus.updateRevenue(orComp.getPublicNumber());
           if (round.getStep() != OperatingRound.STEP_PAYOUT) {
               // The next step is skipped, so update all cash and the share price
               StockChart.refreshStockPanel();
               updatePrice(orCompIndex);
               updateCash(orCompIndex);
               gameStatus.updatePlayerCash();
               gameStatus.updateCompany(orComp.getPublicNumber());
               gameStatus.updateBank();
           }
           
       } else if (command.equals("Payout")) {
           decision[orCompIndex].setText("payout");
           round.fullPayout(orCompName);
           StockChart.refreshStockPanel();
           updatePrice(orCompIndex);
           updateCash(orCompIndex);
           gameStatus.updatePlayerCash();
           gameStatus.updateCompany(orComp.getPublicNumber());
           gameStatus.updateBank();
          
       } else if (command.equals("Split")) {
           decision[orCompIndex].setText("split");
           round.splitPayout(orCompName);
           StockChart.refreshStockPanel();
           updatePrice(orCompIndex);
           updateCash(orCompIndex);
           gameStatus.updatePlayerCash();
           gameStatus.updateCompany(orComp.getPublicNumber());
           gameStatus.updateBank();
           
       } else if (command.equals("Withhold")) {
           decision[orCompIndex].setText("withheld");
           round.withholdPayout(orCompName);
           StockChart.refreshStockPanel();
           updatePrice(orCompIndex);
           updateCash(orCompIndex);
           gameStatus.updateCompany(orComp.getPublicNumber());
           gameStatus.updateBank();
           
       } else if (command.equals("BuyTrain") /*|| done && step == OperatingRound.STEP_BUY_TRAIN*/) {
           /*
           amount = done ? 0 : Integer.parseInt((String)newTrainCostSelect[orCompIndex].getSelectedItem());
           newTrainCost[orCompIndex].setText (amount > 0 ? Bank.format(amount) : "");
           round.buyTrain(companies[orCompIndex].getName(), amount);
           setSelect (newTrainCost[orCompIndex], newTrainCostSelect[orCompIndex], false);
           updateCash();
           gameStatus.updateCompany(orComp.getPublicNumber());
           gameStatus.updateBank();
           */
           ArrayList trainsForSale = new ArrayList();
           trainsForSale.add("None");
           TrainI train;
           int i;

           TrainI[] trains = (TrainI[])TrainManager.get().getAvailableNewTrains().toArray(new TrainI[0]);
           for (i=0; i<trains.length; i++) {
               trainsForSale.add(trains[i].getName()+"-train from IPO at "
                       +Bank.format(trains[i].getCost()));
           }
           trains = Bank.getPool().getUniqueTrains();
           for (i=0; i<trains.length; i++) {
               trainsForSale.add(trains[i].getName()+"-train from Pool at "
                       +Bank.format(trains[i].getCost()));
           }
           for (int j=0; j<nc; j++) {
               c = companies[j];
               if (c == orComp) continue;
               trains = c.getPortfolio().getUniqueTrains();
               for (i=0; i<trains.length; i++) {
                   trainsForSale.add(trains[i].getName()+"-train from "
                           +c.getName());
               }
           }
           
           String boughtTrain = (String)
           		JOptionPane.showInputDialog(this, "Buy which train?", 
           		        "Which train",
           		        JOptionPane.QUESTION_MESSAGE, null,
           		        trainsForSale.toArray(),
           		        trainsForSale.get(1));
           Matcher m = buyTrainPattern.matcher(boughtTrain);
           if (m.matches()) {
               String trainType = m.group(1);
               String sellerName = m.group(2);
               TrainTypeI type = TrainManager.get().getTypeByName(trainType);
               train = null;
               Portfolio seller = null;
               int price = 0;
               if (type != null) {
	               if (sellerName.equals("IPO")) {
	                   seller = Bank.getIpo();
	               } else if (sellerName.equals("Pool")) {
	                   seller = Bank.getPool();
	               } else if (sellerName != null) {
	                   PublicCompanyI sellingComp = 
	                       Game.getCompanyManager().getPublicCompany(sellerName);
	                   if (sellingComp != null) {
	                       seller = sellingComp.getPortfolio();
	                       
	                       String sPrice = JOptionPane.showInputDialog(
	                               this, 
	                               orComp.getName()+" buys "+boughtTrain+" for which price?",
	                               "Which price?", JOptionPane.QUESTION_MESSAGE);
	                       try {
	                           price = Integer.parseInt(sPrice);
	                       } catch (NumberFormatException e) {
	                           price = 0; // Need better error handling!
	                       }
	                   }
	               }
	               
	               if (seller != null) {
	                   train = seller.getTrainOfType(type);
	               }
	               if (train != null) {
	                   if (!round.buyTrain(orComp.getName(), train, price))
	                   {
	                      JOptionPane.showMessageDialog(this, Log.getErrorBuffer());
	                   }
	                   else
	                   {
		                   gameStatus.updateTrains(orComp.getPortfolio());
		                   gameStatus.updateTrains(seller);
		                   gameStatus.updateCash(orComp);
		                   gameStatus.updateCash(Bank.getInstance());
		                   updateCash(orCompIndex);
	
		                   if (seller.getOwner() instanceof PublicCompanyI) {
		                       for (int k=0; k<companies.length; k++) {
		                           if (companies[i] == seller.getOwner()) {
		                               updateCash(k);
		                               gameStatus.updateCash(companies[k]);
		                               break;
		                           }
		                       }
		                   } else if (seller == Bank.getIpo()) {
		                       gameStatus.updateTrains(Bank.getIpo());
		                       gameStatus.updateTrains(Bank.getUnavailable());
		                       
		                       if (TrainManager.get().hasAvailabilityChanged()) {
		                           gameStatus.updateTrains();
		                           TrainManager.get().resetAvailabilityChanged();
		                       }
		                   }
		                   newTrainTotalCost[orCompIndex] += price;
		                   newTrainCost[orCompIndex].setText(""+newTrainTotalCost[orCompIndex]);
		                   
		                   trainsBought.add(train);
		                   newTrains[orCompIndex].setText(TrainManager.get().makeFullList((TrainI[])trainsBought.toArray(new TrainI[0])));
	                   }
	                   
	               }
               }
           }
           
           	
          
       } else if (command.equals("BuyPrivate")) {
           
           Iterator it = Game.getCompanyManager().getAllPrivateCompanies().iterator();
           ArrayList privatesForSale = new ArrayList();
           String privName;
           PrivateCompanyI priv;
           int minPrice = 0, maxPrice = 0;
           while (it.hasNext()) {
               priv = (PrivateCompanyI) it.next();
               if (priv.getPortfolio().getOwner() instanceof Player) {
                   minPrice = (int)(priv.getBasePrice() * orComp.getLowerPrivatePriceFactor());
                   maxPrice = (int)(priv.getBasePrice() * orComp.getUpperPrivatePriceFactor());
                   privatesForSale.add(priv.getName()+" ("
                           +Bank.format(minPrice)+" - "+Bank.format(maxPrice) + ")");
               }
           }
           privName = (String)
				JOptionPane.showInputDialog(this, "Buy which private?", 
					"Which Private?", 
					JOptionPane.QUESTION_MESSAGE,
					null,
					privatesForSale.toArray(),
					privatesForSale.get(0));
           privName = privName.split(" ")[0];
           priv = Game.getCompanyManager().getPrivateCompany(privName);
           minPrice = (int)(priv.getBasePrice() * orComp.getLowerPrivatePriceFactor());
           maxPrice = (int)(priv.getBasePrice() * orComp.getUpperPrivatePriceFactor());
           String price = (String) 
				JOptionPane.showInputDialog(this, "Buy "+privName+" for what price (range "
				        +Bank.format(minPrice)+" - "+Bank.format(maxPrice) + ")?", 
						"What price?", 
						JOptionPane.QUESTION_MESSAGE);
           amount = Integer.parseInt (price);
           Player prevOwner = (Player) priv.getPortfolio().getOwner();
           round.buyPrivate(orComp.getName(), priv.getName(), amount);
           updateCash(orCompIndex);
           gameStatus.updateCash(orComp);
           gameStatus.updateCash(prevOwner);
           
           gameStatus.updateCompanyPrivates(orComp.getPublicNumber());
           gameStatus.updatePlayerPrivates(prevOwner.getIndex());	
           
           
       } else if (command.equals("ClosePrivate")) {
           
           Iterator it = Game.getCompanyManager().getAllPrivateCompanies().iterator();
           ArrayList privatesToClose = new ArrayList();
           PrivateCompanyI priv;
           String privName;
           while (it.hasNext()) {
               priv = (PrivateCompanyI) it.next();
               if (!priv.isClosed()) {
                   privatesToClose.add(priv.getName());
               }
           }
           privName = (String) 
				JOptionPane.showInputDialog(this, "Close which private?", 
					"Which Private?", 
					JOptionPane.QUESTION_MESSAGE,
					null,
					privatesToClose.toArray(),
					privatesToClose.get(0));
           privName = privName.split(" ")[0];
           priv = Game.getCompanyManager().getPrivateCompany(privName);
           CashHolder prevOwner = priv.getPortfolio().getOwner();
           round.closePrivate(privName);
           if (prevOwner instanceof PublicCompanyI) {
               gameStatus.updateCompanyPrivates(((PublicCompanyI)prevOwner).getPublicNumber());
           } else {
               gameStatus.updatePlayerPrivates(((Player)prevOwner).getIndex());
           }
           
       } else if (done /*&& step == OperatingRound.STEP_FINAL*/) {
           round.done(orComp.getName());
          
       }
          
       //gameStatus.updateCompany(orCompIndex);
       //gameStatus.updatePlayers();
       //gameStatus.updateBank();
       
  
       LogWindow.addLog ();
       
       	updateStatus();
        pack();
      
   }
   
   public int getOrCompIndex () {
       return orCompIndex;
   }

   public void setORCompanyTurn (int orCompIndex) {
   	
   		int i, j, share;
   		Color fg, bg;
    		
   		//dummyButton.setSelected(true);
   		
   		if ((j = this.orCompIndex) >= 0) {
   			president[j].setBackground(Color.WHITE);
   			setSelect (tileCost[j], tileCostSelect[j], false);
   			setSelect (tokenCost[j], tokenCostSelect[j], false);
   			setSelect (revenue[j], revenueSelect[j], false);
   		}
   		
   		this.orCompIndex = orCompIndex;
   		orComp = orCompIndex >= 0 ? companies[orCompIndex] : null;
   		orCompName = orComp != null ? orComp.getName() : "";
   		
   		if ((j = this.orCompIndex) >= 0) {
   	   		this.playerIndex = companies[orCompIndex].getPresident().getIndex();
   			president[j].setHighlight(true);
   			setSelect (tileCost[j], tileCostSelect[j], true);
  		}
 		pack();
 		
 		trainsBought = new ArrayList();
   }
   
   public String getSRPlayer () {
   		if (playerIndex >= 0) 
   			return players[playerIndex].getName();
   		else
   			return "";
   }

   private void setSelect (JComponent f, JComponent s, boolean active) {
       f.setVisible(!active);
       s.setVisible(active);
   }
   
   private void updateCash (int index) {
       cash[index].setText(Bank.format(companies[index].getCash()));
   }
   
   private void updatePrice (int index) {
       if (companies[index].hasStockPrice()) {
           sharePrice[index].setText(Bank.format(companies[index].getCurrentPrice().getPrice()));
       }
   }
   
   
}
