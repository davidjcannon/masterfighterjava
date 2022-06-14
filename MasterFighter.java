import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
            switch (command)
            {
                case "start":
                    LoadEClass();
                    break;
                case "points":
                    if (points > 0)
                        PointShop();
                    break;
                case "save":
                    System.out.println("The gave is auto-saved");
                    break;
                case "load":
                    LoadGame();
                    break;
                case "exit":
                case "quit":
                    game = false;
                    break;
                default:
                    System.out.println("That's not a valid option, please try again.");
                    Pause();
                    break;
            }
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

        // Selects random class based on your current level to put you up against
        // Each level gets it's own range of opponents it can go up against
        int cpuclass = switch (level) {
            case 1 -> rand.nextInt(2) + 1;
            case 2 -> rand.nextInt(3) + 1;
            case 3 -> rand.nextInt(2) + 2;
            case 4 -> rand.nextInt(3) + 2;
            case 5 -> rand.nextInt(5) + 2; // Class 6-2
            case 6 -> rand.nextInt(6) + 3;
            // After level 6, it'll pair you up against anyone
            default -> rand.nextInt(fighters.size()) + 1;
        };

        PlayGame(fighters.get(cpuclass-1));
    }

    public static void PlayGame(Fighter cpustats) {
        Random rand = new Random();
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
        int cpuspeedlevel = cpustats.cpuspeedlevel;
        // Sets base levels
        boolean cpuCountered = false;
        boolean cpuCountered2 = false;
        boolean countered = false;
        boolean countered2 = false;
        // Sets base give XP
        int givexp = SetGiveXP(cpustats.givexp, cpustats.nerfxp);
        int refreshed = 0;
        int sameattack = 0;
        int sameattack2 = 0;

        // Everything related to stuns
        int stunRange = 0;
        int cpuStunRange = 0;

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

        int stunResist = (int) Math.round(level*0.75);
        // CPU stun resist is temp, should be replaced with each class having their own custom stun resist
        int cpuStunResist = level/3;

        // Move variables
        int move = 0;
        int move2 = 0;
        int pMove = 0;
        int pMove2 = 0;
        int cpuMove = 0;
        int cpuMove2 = 0;
        boolean backMove = true;
        boolean cpubackMove = true;
        boolean backMove2 = true;
        boolean cpubackMove2 = true;
        // Variables: Don't change ^^
        // Game = 0 means no one won yet, 1 means the player won, 2 means CPU Won, 3 means a tie
        int game = 0;
        // Loading ^^

        while (game == 0) {

            // Changes windows terminal color based on what's currently happening to give information at a glance
            consoleColor('a');
            if (crit==1) consoleColor('e');
            if (cpuCrit==1) consoleColor('d');
            if (cpuStunRange != 0 || stunRange != 0) consoleColor('b');

            move = 0;
            move2 = 0;

            // Checks win conditions
            if (health < 1 && cpuhealth < 1)
                game = 3;
            else if (health < 1)
                game = 2;
            else if (cpuhealth < 1)
                game = 1;

            // Prevents health from going into the negatives
            if (health < 0)
                health = 0;
            if (cpuhealth < 0)
                cpuhealth = 0;

            // Determines whether the damage values have been refreshed yet
            // They must be refreshed because later in the game your damage values may change mid-battle
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
            menuText("Enter \"Back\" to attempt a back attack", backdmg,backfaildmg);
            menuText("Enter \"Blockface\" to counter a face attack", facedmg * 2);
            menuText("Enter \"Blockchest\" to counter a chest attack", chestdmg * 2);
            menuText("Enter \"Blocksweep\" to counter a sweep", sweepdmg * 2);
            menuText("Enter \"Blockback\" to counter a back attack", backdmg * 2);
            //System.out.println("Enter \"Shop\" to buy items"); // Not added yet
            System.out.println("=======================================================Previously=======================================================");

            if (damage > 0 && crit == 1) System.out.println("YOU CRIT (2X DAMAGE)");
            if (cpuDamage > 0 && cpuCrit == 1) System.out.println("YOUR OPPONENT CRIT (2X DAMAGE)");

            if (!cpuCountered && stunRange == 0 && pMove != 0) BuildBattleText(0, pMove, backMove, countered);
            if (!countered && cpuStunRange == 0 && cpuMove != 0) BuildBattleText(1, cpuMove, cpubackMove, cpuCountered);

            if (!cpuCountered2 && stunRange == 0 && pMove2 != 0) BuildBattleText(2, pMove2, backMove2, countered2);
            if (!countered2 && cpuStunRange == 0 && cpuMove2 != 0) BuildBattleText(3, cpuMove2, cpubackMove2, cpuCountered2);

            if (cpuStunRange != 0) System.out.println("OPPONENT STUNNED!");

            if (game == 0)
            {
                if (stunRange == 0) {
                    playerInput = input.nextLine();
                    move = playerMove(playerInput, pMove);

                    // Allows you to use speed 2
                    if (gamespeedlevel > 2) {
                        playerInput = input.nextLine();
                        if (move != 0) {
                            System.out.println("Now enter your second move!");
                            move2 = playerMove(playerInput, pMove);
                        }
                    }
                }
                else
                {
                    timeout(2000);
                    System.out.println("STUNNED");
                        timeout(1500);
                    move = 0;
                    move2 = 0;
                }
            }

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

            if (move != 0 || stunRange != 0)
            {
                // Resets values and gives new randoms
                cpuMove = 0;
                cpuMove2 = 0;
                damage = 0;
                cpuDamage = 0;
                crit = 0;
                cpuCrit = 0;
                backMove = rand.nextBoolean();
                cpubackMove = rand.nextBoolean();;
                backMove2 = rand.nextBoolean();
                cpubackMove2 = rand.nextBoolean();

                // Gets CPU moves
                if (cpuStunRange == 0) {
                    cpuMove = getCpuMove(sameattack, pMove, stunRange);
                    if (cpuspeedlevel > 1)
                        cpuMove2 = getCpuMove(sameattack2, pMove2, stunRange);
                }

                if (cpuStunRange == 0) {
                    // CPU AI that detects repeat moves
                    if (pMove != 0 && pMove == move)
                        sameattack++;
                    if (pMove2 != 0 && pMove2 == move2)
                        sameattack2++;

                    // Removes this angro if you change your moves
                    if (pMove != 0 && pMove != move)
                        sameattack -= 4;
                    if (pMove2 != 0 && pMove2 != move2)
                        sameattack2 -= 4;
                }

                // Sets previous move
                pMove = move;
                pMove2 = move2;

                // Gives XP for current attack
                givexp += GiveXP(move);
                if (gamespeedlevel > 1)
                givexp += (GiveXP(move2))/2;

                // Determines whether the computer countered you
                countered = Countered(move, cpuMove);
                countered2 = Countered(move2, cpuMove2);

                // Determines whether you countered the computer
                cpuCountered = Countered(cpuMove, move);
                cpuCountered2 = Countered(cpuMove2, move2);

                // STUNS:

                // Increments stun ranges for each round they've been up
                if (stunRange > 0)
                    stunRange += 6;
                if (cpuStunRange > 0)
                    cpuStunRange += 6;

                // Attempts to stun CPU

                // Attempts to stun cpu if not already stunned, as long as not countered. Prevents chain stuns
                if (stunRange == 0 && cpuStunRange == 0) {
                    cpuStunRange = calcStun(move, cpuStunRange, countered, cpuStunResist);
                    cpuStunRange = calcStun(move2, cpuStunRange, countered2, cpuStunResist);
                }

                // Attempts to stun you if not already stunned, as long as not countered. Prevents chain stuns
                if (stunRange == 0 && cpuStunRange == 0) {
                    stunRange = calcStun(cpuMove, stunRange, cpuCountered, stunResist);
                    stunRange = calcStun(cpuMove2, stunRange, cpuCountered2, stunResist);
                }

                // Removes the stunned status effect from players
                // Checks if character is stunned, if they are and their stunRange is less or equal to unstun, it'll unstun the player

                int unstun = rand.nextInt(100) + 1; // 1 - 100
                if (stunRange != 0 && unstun <= stunRange)
                    stunRange = 0;

                unstun = rand.nextInt(100) + 1; // 1 - 100
                if (cpuStunRange != 0 && unstun <= cpuStunRange)
                    cpuStunRange = 0;

                // Preforms stun action

                if (stunRange != 0) {
                    move = 0;
                    move2 = 0;
                }
                if (cpuStunRange != 0) {
                    cpuMove = 0;
                    cpuMove2 = 0;
                }

                // ADD: Special ability stuns here

                // Special abilities will go here, skip if cpuStunned

                // Calculates CPU damage

                cpufacedmg = calcDMG(5, cpudamagelevel);
                cpuchestdmg = calcDMG(4, cpudamagelevel);
                cpusweepdmg = calcDMG(3, cpudamagelevel);
                cpubackdmg = calcDMG(10, cpudamagelevel);
                cpubackfaildmg = calcDMG(6, gamedamagelevel);

                // Hit reg

                damage += HitReg(move, facedmg, chestdmg, sweepdmg, backdmg, backfaildmg, countered, cpuCountered, backMove, cpubackMove);
                if (gamespeedlevel > 1)
                damage += HitReg(move2, facedmg, chestdmg, sweepdmg, backdmg, backfaildmg, countered2, cpuCountered2, backMove2, cpubackMove2);
                cpuDamage += HitReg(cpuMove, cpufacedmg, cpuchestdmg, cpusweepdmg, cpubackdmg, cpubackfaildmg, cpuCountered, countered, cpubackMove, backMove);
                if (cpuspeedlevel > 1)
                    cpuDamage += HitReg(cpuMove2, cpufacedmg, cpuchestdmg, cpusweepdmg, cpubackdmg, cpubackfaildmg, cpuCountered2, countered2, cpubackMove2, backMove2);

                // HitReg can only calculate damage you do to the opponent so this is needed to damage yourself when a failed backmove happens
                if (move == 4 && !backMove)
                    cpuDamage += backfaildmg;
                if (cpuMove == 4 && !cpubackMove)
                    damage += cpubackfaildmg;
                // Speed 2
                if (move2 == 4 && !backMove2)
                    cpuDamage += backfaildmg;
                if (cpuMove2 == 4 && !cpubackMove2)
                    damage += cpubackfaildmg;

                // Determines whether a crit can apply
                if (damage > 0 && stunRange == 0)
                    crit = rand.nextInt(critrange) + 1;
                if (cpuDamage > 0 && cpuStunRange == 0)
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
            default:
                if (dropxp)
                    givexp = 2;

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
                if (i == 2)
                {
                    if (level >= 5 && speedlevel == 1) System.out.println("Enter \"Speed\" to upgrade speed from level 1 to level 2 (2 Points)");
                    if (!(level >= 5) && speedlevel == 1) System.out.println("Enter \"Speed\" to upgrade speed from level 1 to level 2 (2 Points / LVL 5+)");
                }
            }

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
            else if (playerInput.equals("speed") && points >= 2)
                Upgrade(playerInput, 2);
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
        else if (upgrade.equals("speed"))
            speedlevel++;
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
        if (otherMove > 4 && move == otherMove - 4)
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

        // Tracks what is being attack
        if (pMove == 1 || pMove == 5)
            word = "face";
        if (pMove == 2 || pMove == 6)
            word = "chest";
        if (pMove == 7)
            word = "feet";
        if (pMove == 8)
            word = "back";

        if (user > 1) {
            System.out.print("SPEED 2: ");
            user -= 2;
        }

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

    // This method is exclusively for back attack text
    static void menuText(String textString, int dmg, int dmg2) {
        System.out.print(textString);
        int stringLength = 98 - textString.length();
        if (dmg > 9)
            stringLength--;
        if (dmg2 > 9)
            stringLength--;
        for (int i = 0; i < stringLength; i++) {
            System.out.print(" ");
        }
            System.out.print("(" + dmg + " DMG / ");
        System.out.println(dmg2 + " FAILED DMG)");
        return;
    }

    static void spacing(String gameString, int dmg) {
        int stringLength = 113 - gameString.length();
        if (dmg > 9)
            stringLength--;
        for (int i = 0; i < stringLength; i++) {
            System.out.print(" ");
        }
        if (dmg > 0)
            System.out.println("(" + dmg + " DMG)");
        return;
    }

    static int playerMove(String playerInput, int pMove)
    {
        int move = 0;
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
            if (temp > 0 && temp <= 8)
                move = temp;
        } catch (Exception ignored) {}
        if (playerInput.equals(""))
            move = pMove;
        return move;
    }

    static int getCpuMove(int sameattack, int pMove, int stunRange)
    {
        Random rand = new Random();
        int cpuMove = 0;
        // CPU AI that responds to repeat moves

        if (stunRange == 0) {
            cpuMove = rand.nextInt(8) + 1;
            int think = 0;
            int thinkRange = (5 - (sameattack - 3));
            if (thinkRange < 2)
                thinkRange = 2;
            if (sameattack >= 3)
                think = rand.nextInt(thinkRange) + 1;
            if (think == 1)
                if (pMove <= 4)
                    cpuMove = pMove + 4;
                else if (pMove == 5)
                    cpuMove = 2;
                else
                    cpuMove = 1;
        } else
            cpuMove = 1;


            return cpuMove;
    }

    static int calcStun(int move, int stunRange, boolean countered, int resist)
    {
        Random rand = new Random();
        int stun = 0;

        if (stunRange == 0 && !countered) {
            // Determines whether to give you a 5% chance at a stun or a 10% chance at a stun
            if (move == 3)
                stun = rand.nextInt(20) + 1;
            else if (move == 4)
                stun = rand.nextInt(10) + 1;

            if (stun == 1) {
                switch (move)
                {
                    case 3 -> stunRange = 15;
                    case 4 -> stunRange = 12;
                }
                // Adds stun resist
                stunRange += resist;
            }
        }
        return stunRange;
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

    // Allows you to change windows terminal color
    static void consoleColor(char color) {
        try {
            new ProcessBuilder("cmd", "/c", "color " + color).inheritIO().start().waitFor();
        } catch (Exception ignored) {}
    }

    static void timeout(int time) {
        try {
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException ignored) {}
    }
}

