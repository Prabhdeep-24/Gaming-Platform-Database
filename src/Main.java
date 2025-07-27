import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

class Queries{
    int getUserIdQuery(Connection con,String userName) throws SQLException{
        String query="Select PlayerId from Player where UserName=?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setString(1, userName);
        ResultSet rs=pstmt.executeQuery();
        if(rs.next()){
            return rs.getInt("PlayerId");
        }
        throw new SQLException("NO USER FOUND WTIH USERNAME: " + userName);
    }

    int getRegionIdQuery (Connection con,String RegionName) throws SQLException{
        String query="Select RegionId from Region where RegionName=?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setString(1, RegionName);
        ResultSet rs=pstmt.executeQuery();
        if(rs.next()){
            return rs.getInt("RegionId");
        }
        throw new SQLException("NO REGION FOUND WITH THIS NAME: " + RegionName);
    }

    void PlayerSkillHistoryQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the User Name: ");
        String UserName=scn.nextLine();
        int userId=getUserIdQuery(con, UserName);
        
        String query="select * from PlayerSkillHistory p join SkillLevel l on p.skillReachedId=l.SkillId where PlayerId= ?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1, userId);

        ResultSet rs=pstmt.executeQuery();

        while (rs.next()) {
            String skillName=rs.getString("SkillName");
            Timestamp date=rs.getTimestamp("RecordedAt");

            System.out.println("PlayerName: "+UserName + " SkillName: "+skillName + " date: "+date);
        }
    }
    
    void PlayerRankHistoryQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the User Name: ");
        String userName=scn.nextLine();
        int userId=getUserIdQuery(con, userName);
        
        String query="select * from PlayerRankingHistory where PlayerId=?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1, userId);
        
        ResultSet rs=pstmt.executeQuery();
    
        while (rs.next()) {
            int ranking=rs.getInt("Ranking");
            Timestamp date=rs.getTimestamp("RecordedAt");
    
            System.out.println("PlayerName: "+userName + " Ranking: "+ranking + " date: "+date);
        }
    }

    void PopularGameByRegionQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the Region Name: ");
        String RegionName=scn.nextLine();
        int RegionId=getRegionIdQuery(con, RegionName);

        String query="with ranking as(\n" + //
                        "\tselect GameId,RegionId,count(*),\n" + //
                        "\trank() over(partition by RegionId order by count(*) desc) as rank\n" + //
                        "\tfrom Matchs\n" + //
                        "\tgroup by GameId,RegionId\n" + //
                        ")\n" + //
                        "select regionName,title\n" + //
                        "from game g\n" + //
                        "join ranking r\n" + //
                        "using (GameId)\n" + //
                        "join Region\n" + //
                        "using (RegionId)\n" + //
                        "where r.rank=1 and RegionId=?";

        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1, RegionId);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            String region=rs.getString("regionName");
            String GameName=rs.getString("title");

            System.out.println("Region Name: "+region + "Game Name: "+GameName);
        }
    }

    void PopularMatchTypeByRegionQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the Region Name: ");
        String RegionName=scn.nextLine();
        int RegionId=getRegionIdQuery(con, RegionName);

        String query="with ranking as(\n" + //
                        "\tselect MatchTypeId,RegionId,count(*),\n" + //
                        "\trank() over(partition by RegionId order by count(*) desc) as 'rank'\n" + //
                        "\tfrom Matchs\n" + //
                        "\tgroup by MatchTypeId,RegionId\n" + //
                        ")\n" + //
                        "select regionName,MatchTypeName\n" + //
                        "from MatchType m\n" + //
                        "join ranking r\n" + //
                        "using (MatchTypeId)\n" + //
                        "join Region\n" + //
                        "using (RegionId)\n" + //
                        "where r.rank=1 and RegionId=?";

        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1, RegionId);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            String region=rs.getString("regionName");
            String MatchTypeName=rs.getString("MatchTypeName");

            System.out.println("Region Name: "+region + "Match Type Name: "+MatchTypeName);
        }
    }

    void PrizeDistributionQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the tournament Id: ");
        int TournamentId=scn.nextInt();
        scn.nextLine();
        String query="select *\n" + //
                        "from PriceDistribution\n" + //
                        "where TournamentId=?";

        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1, TournamentId);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            int position=rs.getInt("position");
            int price=rs.getInt("price");

            System.out.println("Position: "+position+" Price: "+price);
        }
    }

    void tradeVolume(Connection con) throws SQLException{
        String query="SELECT \n" + //
                        "i.Name,\n" + //
                        "COUNT(DISTINCT vt.VirtualTransactionId) AS TotalTrades,\n" + //
                        "SUM(vt.Quantity) AS TotalQuantityTraded\n" + //
                        "FROM VirtualTransaction vt\n" + //
                        "JOIN Item i ON vt.ItemBought = i.ItemId\n" + //
                        "WHERE vt.Action = 'trade'\n" + //
                        "GROUP BY i.ItemId, i.Name;";

        PreparedStatement pstmt=con.prepareStatement(query);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            String itemName=rs.getString("Name");
            int totalTrades=rs.getInt("TotalTrades");
            int TotalQuantityTraded=rs.getInt("TotalQuantityTraded");

            System.out.println("Item Name: "+itemName +" Total Trades: "+ totalTrades + " Total Quantity: "+TotalQuantityTraded);
        }
    }

    void ItemsValuationQuery(Connection con) throws SQLException{
        String query="WITH CoinTrades AS (\n" + //
                        "    SELECT \n" + //
                        "        vt.VirtualTransactionId,\n" + //
                        "        vt1.ItemBought AS ItemId,\n" + //
                        "        SUM(CASE WHEN vt.ItemBought = 1 THEN vt.Quantity ELSE 0 END) AS CoinsGiven,\n" + //
                        "        SUM(CASE WHEN vt1.ItemBought != 1 THEN vt.Quantity ELSE 0 END) AS ItemsReceived\n" + //
                        "    FROM VirtualTransaction vt\n" + //
                        "    join VirtualTransaction vt1\n" + //
                        "    GROUP BY vt.VirtualTransactionId,vt1.ItemBought\n" + //
                        "    HAVING CoinsGiven > 0 AND ItemsReceived > 0\n" + //
                        ")\n" + //
                        "SELECT \n" + //
                        "    i.Name AS ItemName,\n" + //
                        "    COUNT(*) AS NumberOfTrades,\n" + //
                        "    ROUND(SUM(CoinsGiven * 1.0) / SUM(ItemsReceived), 2) AS EstimatedAvgValueInCoins\n" + //
                        "FROM CoinTrades ct\n" + //
                        "JOIN Item i ON ct.ItemId = i.ItemId\n" + //
                        "GROUP BY i.ItemId, i.Name;";
        PreparedStatement pstmt=con.prepareStatement(query);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            String item=rs.getString("ItemName");
            int numOfTrades=rs.getInt("NumberOfTrades");
            double estTradeValue=rs.getDouble("EstimatedAvgValueInCoins");

            System.out.println("Item Name: "+item+" Number of Trades: "+numOfTrades+ " Estimated Trade Value:"+estTradeValue);
        }
    }

    void CheckPlayerIsBanQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the userName of the user: ");
        String userName=scn.nextLine();
        int userId=getUserIdQuery(con, userName);

        String query="Select IsBanned from Player where PlayerId=?";
        PreparedStatement pstmt=con.prepareStatement(query);

        pstmt.setInt(1, userId);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            System.out.println(rs.getBoolean("IsBanned"));
        }
    }

    void getModerationActionAgainstUserQuery(Connection con,Scanner scn) throws SQLException{
        System.out.print("Enter the userName of the user: ");
        String userName=scn.nextLine();
        int userId=getUserIdQuery(con, userName);

        String query="select *\n" + //
                    "from ModerationAction m\n" + //
                    "join Actions a\n" + //
                    "using(ActionId)\n" + //
                    "where playerId=?";
        PreparedStatement pstmt=con.prepareStatement(query);
        pstmt.setInt(1, userId);

        ResultSet rs=pstmt.executeQuery();
        while(rs.next()){
            int reporterId=rs.getInt("ReporterId");
            String ActionType=rs.getString("ActionName");
            Timestamp dateTime=rs.getTimestamp("Timestamp");

            System.out.println("ReportedId: "+reporterId + "Action Type: "+ActionType+" Date-time: "+dateTime);
        }
    }

    void IncomeFromSubscription(Connection con) throws SQLException{
        String query="select date_format(PurchaseDate,'%Y-%m') as mname,sum(amount) as totalAmount\n" + //
                        "from Subscription s\n" + //
                        "join SubscriptionPacks p\n" + //
                        "using (SubscriptionPackId)\n" + //
                        "group by date_format(PurchaseDate,'%Y-%m');";
        
        PreparedStatement pstmt=con.prepareStatement(query);
        ResultSet rs=pstmt.executeQuery();

        while(rs.next()){
            String monthName=rs.getString("mname");
            int totalAmount=rs.getInt("totalAmount");

            System.out.println("Month: " +monthName + " Income: "+totalAmount);
        }
    }
} 

public class Main{

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Properties props = new Properties();
        try {
            InputStream input = Main.class.getClassLoader().getResourceAsStream("db.properties"); // Changed line
            if (input == null) {
                System.out.println("Error: db.properties not found in the classpath. Make sure it's bundled correctly.");
                return; // Or throw a more appropriate exception
            }
            props.load(input);
    
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            String driver = props.getProperty("db.driver");
    
            Class.forName(driver);
            Scanner scn=new Scanner(System.in);
            Queries query=new Queries();
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
    
                Connection con=DriverManager.getConnection(url, user, password);
                System.out.println("Connection established");
                while(true){
                    System.out.println("Gaming Platform Queries Menu:");
                    System.out.println("1: PlayerSkillHistoryQuery");
                    System.out.println("2: PlayerRankHistoryQuery");
                    System.out.println("3: PopularGameByRegionQuery");
                    System.out.println("4: PopularMatchTypeByRegionQuery");
                    System.out.println("5: PrizeDistributionQuery");
                    System.out.println("6: tradeVolume");
                    System.out.println("7: ItemsValuationQuery");
                    System.out.println("8: CheckPlayerIsBanQuery");
                    System.out.println("9: getModerationActionAgainstUserQuery");
                    System.out.println("10: IncomeFromSubscription");
                    System.out.println("0: exit the program");
        
                    int choice=scn.nextInt();
                    scn.nextLine();
        
                    switch(choice){
                        case 1: 
                            query.PlayerSkillHistoryQuery(con, scn);
                            break;
                        case 2: 
                            query.PlayerRankHistoryQuery(con, scn);
                            break;
                        case 3: 
                            query.PopularGameByRegionQuery(con, scn);
                            break;
                        case 4: 
                            query.PopularMatchTypeByRegionQuery(con, scn);
                            break;
                        case 5: 
                            query.PrizeDistributionQuery(con, scn);
                            break;
                        case 6: 
                            query.tradeVolume(con);
                            break;
                        case 7:
                            query.ItemsValuationQuery(con);
                            break;
                        case 8:     
                            query.CheckPlayerIsBanQuery(con, scn);
                            break;
                        case 9: 
                            query.getModerationActionAgainstUserQuery(con, scn);
                            break;
                        case 10:
                            query.IncomeFromSubscription(con);
                            break;
                        case 0:
                            con.close();
                            return;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}