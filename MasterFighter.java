import java.io.*;
import java.util.*;

public class MasterFighter {
    // Loads everything that will be needed globally
    public static int level = 1;
    public static int xp = 0;
    public static int points = 0;
    public static int healthlevel = 1;
    public static int speedlevel = 1;
    public static int damagelevel = 1;
    public static int money = 0;

    public static void main(String[] args) {
        LoadGame();
        Scanner input = new Scanner(System.in);
        Random rand = new Random();
        boolean game = true;
        // CRITICAL: Don't change ^^
        String command = "";
        // Variables: Don't change ^^
        String version = "1.0.0";
        boolean debug = true;
        String[] splashes = {"Fight!", "Speed!", "Strength!", "Gotta be the strongest!", "Don't give up!", "Power Up!", "Stand up and fight!", "Give it your all!"};
        // Loading ^^
        if (debug == false) {
            System.out.println("Welcome to Master Fighter (JE) V. " + version);
            System.out.println("Created By David Cannon");
            Pause();
        }
        while (game) {
            SaveGame();
            consoleColor('a');
            if (xp >= nextLevelXP())
                LevelUp();
            int splash = rand.nextInt(splashes.length);
            // Menu loading ^^
            cls();
            System.out.println("========================================================================================================================");
            System.out.println("                                                Master Fighter Main Menu");
            System.out.println("========================================================================================================================");
            // Gives random text each time you return to the main menu
            System.out.println(splashes[splash]);
            System.out.println("==========================================================Stats=========================================================");
            System.out.println("Level: " + level);
            System.out.println("XP: " + xp + " / " + nextLevelXP());
            System.out.println("Health Level: " + healthlevel);
            System.out.println("Damage Level: " + damagelevel);
            System.out.println("Speed Level: " + speedlevel);
            System.out.println("========================================================================================================================");
            System.out.println("Enter \"Start\" to begin a match");
            if (points > 0) System.out.println("Enter \"Points\" to spend your skill points");
            command = input.nextLine();
            command = command.toLowerCase();

            // Commands
            if (command.equals("start"))
                LoadEClass();
            else if (command.equals("points") && points > 0)
                PointShop();
            else if (command.equals("save"))
                SaveGame();
            else if (command.equals("load"))
                LoadGame();
            else
            Pause();
        }
    }

    // Loads a random class
    public static void LoadEClass() {
        Random rand = new Random();
        ArrayList<Fighter> fighters = new ArrayList<>();

        // Loads fighters
        fighters.add(new Fighter((fighters.size()+1), "Weakling", 80, 1, 1, 20, 4, 200));
        // Cuts off at lvl 3
        fighters.add(new Fighter((fighters.size()+1), "Normal",1,1,1, 24,4,100));
        // Level 1 Classes ^^ (Cuts off at level 6)
        fighters.add(new Fighter((fighters.size()+1), "Berserker (Young)", 90, 2, 1,28,5,26));
        // Level 2 Classes ^^ (Cuts off at level 8)
        fighters.add(new Fighter((fighters.size()+1), "Fighter", 150, 2, 1,24,5,34));
        // Level 4 Classes ^^
        fighters.add(new Fighter((fighters.size()+1), "Tank", 200, 1, 1,28,7,30));
        fighters.add(new Fighter((fighters.size()+1), "Berserker", 1, 3, 1,32,8, 20));
        fighters.add(new Fighter((fighters.size()+1), "Speedy", 1, 1, 2,26,7, 30));
        fighters.add(new Fighter((fighters.size()+1), "Mage", 120, 1, 1,30,9,24));

        // 8 CLASSES ATM

        int cpuclass = switch (level) {
            case 1 -> rand.nextInt(1);
            case 2 -> rand.nextInt(2);
            case 3 -> rand.nextInt(3) + 1;
            case 4 -> rand.nextInt(4) + 1;
            case 5 -> rand.nextInt(6) + 1; // Class 6-2, 5-1
            case 6 -> rand.nextInt(7) + 2;
            default -> rand.nextInt(fighters.size()) + 1;
        };

        PlayGame(fighters.get(cpuclass));
    }

    public static void PlayGame(Fighter cpustats) {
        Random rand = new Random();
        String gameString;
        Scanner input = new Scanner(System.in);
        String playerInput = "";
        // CRITICAL: Don't change ^^

        // Sets health
        int maxhealth = SetHealth(healthlevel);
        int cpumaxhealth = SetHealth(cpustats.cpuhealthlvl);

        int health = maxhealth;
        int cpuhealth = cpumaxhealth;

        int gamedamagelevel = damagelevel;
        int cpudamagelevel = cpustats.cpudamagelevel;
        // Sets damage ^^
        int gamespeedlevel = speedlevel;
        // Sets base levels
        int move = 0;
        int pMove = 0;
        boolean cpuCountered = false;
        boolean countered = false;
        // Sets base give XP
        int givexp = SetGiveXP(cpustats.givexp, cpustats.nerfxp);
        int refreshed = 0;
        int sameattack = 0;

        // Player DMG Values
        int facedmg = 0;
        int chestdmg = 0;
        int sweepdmg = 0;
        int backdmg = 0;
        int backfaildmg = 0;

        // CPU DMG Values
        int cpufacedmg = 0;
        int cpuchestdmg = 0;
        int cpusweepdmg = 0;
        int cpubackdmg = 0;
        int cpubackfaildmg = 0;

        int damage = 0;
        int cpuDamage = 0;

        int crit = 0;
        int cpuCrit = 0;
        int critrange = 24-level;
        if (level <= 3)
            critrange += 27;
        if (level <= 7)
            critrange += 15;
        int cpuCritRange = cpustats.crit;
        // Crit variables ^^

        int cpuMove = 0;
        boolean backMove = true;
        boolean cpubackMove = true;
        // Variables: Don't change ^^
        // Game = 0 means no one won yet, 1 means you won, 2 means CPU Won, 3 means a tie
        int game = 0;
        // Loading ^^
        while (game == 0) {
            consoleColor('a');
            if (crit==1) consoleColor('e');
            if (cpuCrit==1) consoleColor('d');
            if (health < 1 && cpuhealth < 1)
                game = 3;
            else if (health < 1)
                game = 2;
            else if (cpuhealth < 1)
                game = 1;
            if (health < 0)
                health = 0;
            if (cpuhealth < 0)
                cpuhealth = 0;
            if (refreshed == 0) {
                facedmg = calcDMG(5, gamedamagelevel);
                chestdmg = calcDMG(4, gamedamagelevel);
                sweepdmg = calcDMG(3, gamedamagelevel);
                backdmg = calcDMG(10, gamedamagelevel);
                backfaildmg = calcDMG(6, cpudamagelevel);
                refreshed = 1;
            }
            cls();
            // Game UI
            System.out.println("Opponent Class: " + cpustats.classname);
            System.out.print("Player Health: " + health);
            if (cpuDamage > 0)
            System.out.print(" (Damage Taken: " + cpuDamage + ")");
            System.out.println();
            System.out.print("Opponent Health: " + cpuhealth);
            if (damage > 0)
            System.out.print(" (Damage Taken: " + damage + ")");
            System.out.println();
            menuText("Enter \"Face\" to hit your opponent in the face", facedmg);
            menuText("Enter \"Chest\" to hit your opponent in the chest", chestdmg);
            menuText("Enter \"Sweep\" to sweep your opponent", sweepdmg);
            menuText("Enter \"Back\" to attempt a back attack", 0);
            System.out.println("(" + backdmg + " DMG / " + backfaildmg + " FAILED DMG)");
            menuText("Enter \"Blockface\" to counter a face attack", facedmg * 2);
            menuText("Enter \"Blockchest\" to counter a chest attack", chestdmg * 2);
            menuText("Enter \"Blocksweep\" to counter a sweep", sweepdmg * 2);
            menuText("Enter \"Blockback\" to counter a back attack", backdmg * 2);
            //System.out.println("Enter \"Shop\" to buy items"); // Not added yet
            System.out.println("=======================================================Previously=======================================================");
            if (damage > 0 && crit == 1) System.out.println("YOU CRIT (2X DAMAGE)");
            if (cpuDamage > 0 && cpuCrit == 1) System.out.println("YOUR OPPONENT CRIT (2X DAMAGE)");
            if (pMove != 0) {
                if (!cpuCountered) BuildBattleText(0, pMove, backMove, countered);
                if (!countered) BuildBattleText(1, cpuMove, cpubackMove, cpuCountered);
            }

            if (game == 0)
                playerInput = input.nextLine();

                // Winning screens
            else {
                if (game > 1)
                    consoleColor('c');
                Pause();
                int displayXP = calcXP(givexp, game);
                cls();
                switch (game)
                {
                    case 1:
                        System.out.println("You've successfully defeated your opponent and won the fight!");
                        break;
                    case 2:
                        System.out.println("You've been defeated!");
                        break;
                    case 3:
                        System.out.println("You both knocked each other out!");
                        System.out.println("You both clearly have met your match!");
                        break;
                }
                System.out.println("You've been given " + displayXP + " XP");
                Pause();
                consoleColor('a');
                break;
            }

            playerInput = playerInput.toLowerCase();
            move = switch (playerInput) {
                case "face" -> 1;
                case "chest" -> 2;
                case "sweep" -> 3;
                case "back" -> 4;
                case "blockface" -> 5;
                case "blockchest" -> 6;
                case "blocksweep" -> 7;
                case "blockback" -> 8;
                default -> 0;
            };
            try {
                int temp = Integer.parseInt(playerInput);
                if (temp > 0 && temp < 8)
                    move = temp;
            } catch (Exception ignored) {
            }
            if (playerInput.equals(""))
                move = pMove;
            if (move != 0) {

                // Resets values and gives new randoms
                damage = 0;
                cpuDamage = 0;
                crit = 0;
                cpuCrit = 0;
                backMove = rand.nextBoolean();
                cpubackMove = rand.nextBoolean();
                cpuMove = rand.nextInt(8) + 1;

                // CPU AI that responds to repeat moves
                int think = 0;
                int thinkRange = (5-(sameattack-3));
                if (thinkRange < 2)
                    thinkRange = 2;
                if (sameattack >= 3)
                    think = rand.nextInt(thinkRange) + 1;
                if (think == 1)
                    if (pMove <= 4)
                    cpuMove = pMove+4;
                else if (pMove == 5)
                    cpuMove = 2;
                else
                    cpuMove = 1;

                // CPU AI that detects repeat moves
                if (pMove != 0 && pMove == move)
                    sameattack++;

                // Sets previous move
                pMove = move;

                // Gives XP for current attack
                givexp += GiveXP(move);

                // Determines whether the computer countered you
                countered = Countered(move, cpuMove);
                // Determines whether you countered the computer
                cpuCountered = Countered(cpuMove, move);

                // Calculates CPU damage

                cpufacedmg = calcDMG(5, cpudamagelevel);
                cpuchestdmg = calcDMG(4, cpudamagelevel);
                cpusweepdmg = calcDMG(3, cpudamagelevel);
                cpubackdmg = calcDMG(10, cpudamagelevel);
                cpubackfaildmg = calcDMG(6, gamedamagelevel);

                // Hit reg

                damage += HitReg(move, facedmg, chestdmg, sweepdmg, backdmg, backfaildmg, countered, cpuCountered, backMove, cpubackMove);
                cpuDamage += HitReg(cpuMove, cpufacedmg, cpuchestdmg, cpusweepdmg, cpubackdmg, cpubackfaildmg, cpuCountered, countered, cpubackMove, backMove);

                // HitReg can only calculate damage you do to the opponent so this is needed to damage yourself when you mess up your back move
                if (move == 4 && !backMove)
                    cpuDamage += backfaildmg;
                if (cpuMove == 4 && !cpubackMove)
                    damage += cpubackfaildmg;

                // Determines whether a crit can apply
                if (damage > 0)
                    crit = rand.nextInt(critrange) + 1;
                if (cpuDamage > 0)
                    cpuCrit = rand.nextInt(cpuCritRange) + 1;

                // Applies crit
                if (crit == 1) {
                    givexp += 4;
                    damage *= 2;
                }
                if (cpuCrit == 1)
                    cpuDamage *= 2;

                health -= cpuDamage;
                cpuhealth -= damage;

                refreshed = 0;
            } else {
                System.out.println("That wasn't a option, try again");
                Pause();
            }

        }
    }

    public static class Fighter {
        int classID;
        int cpuhealthlvl;
        int cpudamagelevel;
        int cpuspeedlevel;
        String classname;
        int givexp;
        int nerfxp;
        int crit;

        public Fighter() {
            this.classID = 0;
            this.cpuhealthlvl = 1;
            this.cpudamagelevel = 1;
            this.cpuspeedlevel = 1;
            this.classname = "Unnamed Class";
            this.givexp = 0;
            this.nerfxp = 0;
            this.crit = 9999;
        }

        public Fighter(int classID, String classname) {
            this.classID = classID;
            this.cpuhealthlvl = 1;
            this.cpudamagelevel = 1;
            this.cpuspeedlevel = 1;
            this.classname = classname;
            this.givexp = 0;
            this.nerfxp = 0;
            this.crit = 9999;
        }

        public Fighter(int classID, String classname, int cpuhealthlvl, int cpudamagelevel, int cpuspeedlevel, int givexp, int nerfxp, int crit) {
            this.classID = classID;
            this.cpuhealthlvl = cpuhealthlvl;
            this.cpudamagelevel = cpudamagelevel;
            this.cpuspeedlevel = cpuspeedlevel;
            this.classname = classname;
            this.givexp = givexp;
            this.nerfxp = nerfxp;
            this.crit = crit;
        }
    }

    static int HitReg(int move, int facedmg, int chestdmg, int sweepdmg, int backdmg, int backfaildmg,
                      boolean countered, boolean otherCountered, boolean backMove, boolean otherbackMove) {
        int damage = 0;
        if (!countered) {
            switch (move) {
                case 1:
                    damage += facedmg;
                    break;
                case 2:
                    damage += chestdmg;
                    break;
                case 3:
                    damage += sweepdmg;
                    break;
                case 4:
                    if (backMove)
                        damage += backdmg;
                    break;

            }
        }

        if (otherCountered) {
            switch (move) {
                case 5:
                    damage += facedmg * 2;
                    break;
                case 6:
                    damage += chestdmg * 2;
                    break;
                case 7:
                    damage += sweepdmg * 2;
                    break;
                case 8:
                    if (otherbackMove)
                        damage += backdmg * 2;
                    else
                        damage += backfaildmg * 2;
                    break;

            }
        }
        return damage;
    }

    // Determines base XP each class will give
    static int SetGiveXP(int givexp, int nerflvl)
    {
        if (level >= nerflvl)
            givexp = givexp - (level - (nerflvl - 1) * 7);

        if (givexp > 1)
            givexp = 1;
        return givexp;
    }

    // Determines how much XP you get per-move
    static int GiveXP(int move) {
        Random rand = new Random();
        int givexp = 0;
        boolean dropxp = rand.nextBoolean();
        switch (move) {
            // Face attack
            case 1:
                // XP until level 3
                if (level < 3)
                    givexp = 2;
                    // XP until level 6
                else if (level < 6)
                    givexp = 1;
                    // XP for the rest of the game
                else if (dropxp)
                    givexp = 1;
                break;

            // Chest attack
            case 2:
                // XP until level 4
                if (level < 4)
                    givexp = 2;
                    // XP until level 7
                else if (level < 7)
                    givexp = 1;
                    // XP for the rest of the game
                else if (dropxp)
                    givexp = 1;
                break;

            // Sweep attack
            case 3:
                // XP until level 3
                if (level < 3)
                    givexp = 3;
                    // XP until level 8
                else if (level < 8)
                    givexp = 2;
                    // XP for the rest of the game
                else
                    givexp = 1;
                break;

            // Back attack
            case 4:
                // XP until level 3
                if (level < 4)
                    givexp = 4;
                    // XP until level 6
                else if (level < 6)
                    givexp = 3;
                    // XP until level 9
                else if (level < 9)
                    givexp = 2;
                    // XP for the rest of the game
                else
                    givexp = 1;
                break;

        }
        return givexp;
    }

    // Stores how to calculate next level xp
    static int nextLevelXP() { return 100+((level-1)*75)+((level/5)*75);}

    // Levels up character
    static void LevelUp()
    {
        xp -= nextLevelXP();
        level++;
        points++;
        cls();
        System.out.println("You leveled up!");
        System.out.println("You are now level " + level + "!");
        System.out.println("You now have " + xp + " XP!");
        System.out.println("You earned 1 point");
        if (points > 1)
        System.out.println("You now have a total of " + points + " points!");
        System.out.println("You need " + nextLevelXP() + " XP to level up again");
        Pause();
        PointShop();
    }

    static void PointShop()
    {
        while (true) {
            if (points == 0) break;
            Scanner input = new Scanner(System.in);
            String playerInput = "";
            int healthUpgradeCost = 0;
            int damageUpgradeCost = damagelevel;
            int speedUpgradeCost = 0;
            if (healthlevel == 1)
                healthUpgradeCost = 1;
            else
                healthUpgradeCost = 2;
            cls();
            System.out.println("What would you like to spend your skill point(s) on?");
            System.out.println("Skill Points Avaliable: " + points);

            // Sorts the points menu by what's currently the cheapest being at the top
            for (int i = 1; i <= 3; i++) {
                if (healthUpgradeCost == i)
                    System.out.println("Enter \"Health\" to upgrade health from level " + healthlevel +
                            " to level " + (healthlevel + 1) + " (" + i + " Point(s)) (" + SetHealth(healthlevel + 1) + " HP) +"
                            + (SetHealth(healthlevel + 1) - SetHealth(healthlevel)) + " HP");
                if (damageUpgradeCost == i)
                    System.out.println("Enter \"Damage\" to upgrade damage from level " + damagelevel + " to level " + (damagelevel + 1) + " (" + i + " Point(s))");
            }

            // Speed upgrade (Not added yet)
            //if (level >= 5 && speedlevel == 1) System.out.println("Enter \"Speed\" to upgrade speed from level 1 to level 2 (2 Point)");
            //if (!(level >= 5) && speedlevel == 1) System.out.println("Enter \"Speed\" to upgrade speed from level 1 to level 2 (2 Points / LVL 5+)");

            if (damagelevel == 3)
                System.out.println("Enter \"Damage\" to upgrade damage from level 3 to level 4 (3 Point)");

            System.out.println("Enter \"Exit\" to return to the main menu");

            // Shows levels and whether they're maxed

            System.out.print("Health Level: " + healthlevel);
            if (healthlevel == 5) System.out.print(" (MAX)");
            System.out.println();
            System.out.print("Damage Level: " + damagelevel);
            if (damagelevel == 4) System.out.print(" (MAX)");
            System.out.println();
            System.out.print("Speed Level: " + speedlevel);
            if (speedlevel == 3) System.out.print(" (MAX)");
            System.out.println();

            playerInput = input.nextLine();
            playerInput = playerInput.toLowerCase();

            if (playerInput.equals("damage") && points >= damageUpgradeCost)
                Upgrade(playerInput, damageUpgradeCost);
            else if (playerInput.equals("health") && points >= healthUpgradeCost)
                Upgrade(playerInput, healthUpgradeCost);
            else if (playerInput.equals("exit"))
                break;
            else {
                System.out.println("Either " + playerInput + " isn't a option or you don't have enough points, try again.");
                Pause();
            }
        }
    }

    static void Upgrade(String upgrade, int cost)
    {
        if (upgrade.equals("health"))
            healthlevel++;
        else if (upgrade.equals("damage"))
            damagelevel++;
        points -= cost;
    }

    // Determines what your max health should be based on your level
    static int SetHealth(int hplvl) {
        return switch (hplvl) {
            default -> hplvl;
            case 1 -> 100;
            case 2 -> 125;
            case 3 -> 175;
            case 4 -> 225;
            case 5 -> 275;
            case 6 -> 350;
        };
    }

    // Calculates damage
    static int calcDMG(int baseDMG, int gamedamagelevel) {
        return baseDMG + (baseDMG / 2) * (gamedamagelevel - 1);
    }

    // Calculates and sets xp
    static int calcXP(int givexp, int game) {
        switch (game) {
            // XP Modifiers
            // Game lost
            case 2 -> givexp = givexp / level;
            // Game tied
            case 3 -> givexp = (int) Math.round(givexp * (1 - (level * 0.04)));
        };
        xp += givexp;
        return givexp;
    }

    static boolean Countered(int move, int otherMove) {
        if (move == otherMove - 4)
            return true;
        return false;
    }

    static void SaveGame()
    {
        cls();
        //System.out.println("Saving game...");
        ArrayList<Integer> saveData = new ArrayList<Integer>(
                Arrays.asList(level, xp, points, healthlevel, damagelevel, speedlevel, money));
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("saveData.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(saveData);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            System.out.println("Game failed to save");
            Pause();
        }
    }

    static void LoadGame() {
        System.out.println("Loading save file...");
        File file = new File("saveData.dat");
        if (!file.exists()) return;
        ArrayList saveData = null;
        try {
            FileInputStream fileIn = new FileInputStream("saveData.dat");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            saveData = (ArrayList) in.readObject();
            in.close();
            fileIn.close();
            for (int i = 0; i < saveData.size(); i++) {
                //System.out.println(saveData.get(i).toString());
                // Order: level, xp, points, healthlevel, damagelevel, speedlevel, money
                switch (i) {
                    case 0 -> level = (int) saveData.get(i);
                    case 1 -> xp = (int) saveData.get(i);
                    case 2 -> points = (int) saveData.get(i);
                    case 3 -> healthlevel = (int) saveData.get(i);
                    case 4 -> damagelevel = (int) saveData.get(i);
                    case 5 -> speedlevel = (int) saveData.get(i);
                    case 6 -> money = (int) saveData.get(i);
                }
            }
        } catch (IOException | ClassNotFoundException ignored) {
            System.out.println("Game failed to load");
            Pause();
        }
    }

    static void BuildBattleText(int user, int pMove, boolean backMove, boolean countered)
    {
        //System.out.println(pMove + " " + backMove + " " + countered);
        String word = "";
        String userText = "";
        String otherText = "";

        // Tracks
        if (pMove == 1 || pMove == 5)
            word = "face";
        if (pMove == 2 || pMove == 6)
            word = "chest";
        if (pMove == 7)
            word = "feet";
        if (pMove == 8)
            word = "back";

        // Determines whether this is you or your opponent
        if (user == 0) {
            System.out.print("You ");
            otherText = "your opponent";
            userText = "your ";
        }
        else {
            System.out.print("Your opponent ");
            otherText = "you";
            userText = "their ";
        }

        // Builds the start of the sentence (You/your)

        // Blocked text
        if (!countered && pMove > 4)
            System.out.print("blocked " + userText + word);

        // Determines whether to add that it was an attempt
        if (countered || (pMove==4))
            System.out.print("tried to ");

        // Face or chest

        if (pMove <= 2 || pMove == 4)
            System.out.print("hit " + otherText + " ");

        if (pMove <= 2)
            System.out.print("in the " + word);

        if (pMove == 4) {
            String tempWord = "";
            if (backMove)
                tempWord = "and succeeded";
            else
                tempWord = "but failed";
            System.out.print("from behind " + tempWord);
        }

        // Sweeping
        if (pMove == 3 && countered)
            System.out.print("sweep " + otherText);
        else if (pMove == 3)
            System.out.print("swept " + otherText);

        System.out.print("!");

        // Adds counter if there was a counter
        if (countered)
            System.out.print(" (COUNTERED)");

        System.out.println();
    }

    // Menu elements
    static void menuText(String textString, int dmg) {
        System.out.print(textString);
        spacing(textString, dmg);
        return;
    }
    static void spacing(String gameString, int dmg) {
        int stringLength = 113 - gameString.length();
        if (dmg > 9)
            stringLength--;
        else if (dmg == 0)
            stringLength -= 16;
        for (int i = 0; i < stringLength; i++) {
            System.out.print(" ");
        }
        if (dmg > 0)
            System.out.println("(" + dmg + " DMG)");
        return;
    }

    // Allows for easy pauses
    static void Pause() {
        Scanner input = new Scanner(System.in);
        System.out.println("Press enter to continue...");
        input.nextLine();
    }

    // Clears screen in windows terminal
    static void cls() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception ignored) {}
    }

    static void consoleColor(char color) {
        try {
            new ProcessBuilder("cmd", "/c", "color " + color).inheritIO().start().waitFor();
        } catch (Exception ignored) {
        }
    }
}
