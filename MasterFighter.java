import java.io.IOException;
import java.util.*;

public class MasterFighter {
    // Loads everything that will be needed globally
    public static int level = 1;
    public static int healthlevel = 1;
    public static int speedlevel = 1;
    public static int damagelevel = 1;
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        Random rand = new Random();
        boolean game = true;
        // CRITICAL: Don't change ^^
        String command = "";
        int xp = 0;
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
            if (debug = true) {
                LoadEClass();
            }
            int splash = rand.nextInt(splashes.length);
            // Menu loading ^^
            cls();
            System.out.println("========================================================================================================================");
            System.out.println("                                                Master Fighter Main Menu");
            System.out.println("========================================================================================================================");
            // Gives random text each time you return to the main menu
            System.out.println(splashes[splash]);
            System.out.println("==========================================================Stats=========================================================");
            System.out.println("Stats have not been added yet");
            System.out.println("========================================================================================================================");
            System.out.println("Type \"Start\" to begin a match");
            command = input.nextLine();
            command = command.toLowerCase();
            if (command.equals("start"))
                LoadEClass();
            Pause();
        }
    }

    // Loads a random class
    public static void LoadEClass()
    {
        Random rand = new Random();
        ArrayList<Fighter> fighters = new ArrayList<>();

        // Loads fighters
        fighters.add(new Fighter(1, "Normal"));
        fighters.add(new Fighter(2, "Normal", 0,1,1));

        int cpuclass = rand.nextInt(fighters.size());

        PlayGame(fighters.get(cpuclass));
    }

    public static void PlayGame(Fighter cpustats)
    {
        Random rand = new Random();
        String gameString;
        Scanner input = new Scanner(System.in);
        String playerInput;
        // CRITICAL: Don't change ^^
        int maxhealth = SetHealth(healthlevel);
        int health = maxhealth;
        int cpumaxhealth = SetHealth(cpustats.cpuhealthlvl);
        int cpuhealth = cpumaxhealth;
        int move;
        // Variables: Don't change ^^
        boolean game = true;
        // Loading ^^
        while (game)
        {
            move = 0;
            cls();
            // Game UI
            System.out.println("Opponent Class: " + cpustats.classname);
            System.out.println("Player Health: " + health);
            System.out.println("Opponent Health: " + cpuhealth);
            menuText("Enter \"Face\" to hit your opponent in the face", 5);
            menuText("Enter \"Chest\" to hit your opponent in the chest", 4);
            menuText("Enter \"Sweep\" to sweep your opponent",3);
            menuText("Enter \"Back\" to attempt a back attack",0);
            System.out.println("(10 DMG / 6 FAILED DMG)");
            menuText("Enter \"Blockface\" to counter a face attack", 10);
            menuText("Enter \"Blockchest\" to counter a chest attack", 8);
            menuText("Enter \"Blocksweep\" to counter a sweep", 6);
            menuText("Enter \"Blockback\" to counter a back attack", 20);
            //System.out.println("Enter \"Shop\" to buy items"); // Not added yet
            //System.out.println("=======================================================Previously=======================================================");
            System.out.println("========================================================================================================================");
            playerInput = input.nextLine();
            playerInput = playerInput.toLowerCase();
            try {
                move = Integer.parseInt(playerInput);
            } catch(Exception e) {}
            switch (playerInput)
            {
                case "face":
                    move = 1;
                    break;
                case "chest":
                    move = 2;
                    break;
                case "sweep":
                    move = 3;
                    break;
                case "back":
                    move = 4;
                break;
                case "blockface":
                    move = 5;
                    break;
                case "blockchest":
                    move = 6;
                    break;
                case "blocksweep":
                    move = 7;
                    break;
                case "blockback":
                    move = 8;
                    break;
            }
            if (move != 0)
                GameValidAttack();

            System.out.println("That wasn't a option, try again");
            Pause();

        }
    }

    public static class Fighter
    {
        int classID;
        int cpuhealthlvl;
        int cpudamagelevel;
        int cpuspeedlevel;
        String classname;
        int givexp;

        public Fighter() {
            this.classID = 0;
            this.cpuhealthlvl = 1;
            this.cpudamagelevel = 1;
            this.cpuspeedlevel = 1;
            this.classname = "Unnamed Class";
            this.givexp = GiveXP(classID);
        }

        public Fighter(int classID, String classname) {
            this.classID = classID;
            this.cpuhealthlvl = 1;
            this.cpudamagelevel = 1;
            this.cpuspeedlevel = 1;
            this.classname = classname;
            this.givexp = GiveXP(classID);
        }

        public Fighter(int classID, String classname, int cpuhealthlvl, int cpudamagelevel, int cpuspeedlevel) {
            this.classID = classID;
            this.cpuhealthlvl = cpuhealthlvl;
            this.cpudamagelevel = cpudamagelevel;
            this.cpuspeedlevel = cpuspeedlevel;
            this.classname = classname;
            this.givexp = GiveXP(classID);
        }
    }

    static void GameValidAttack()
    {
        PlayGame.heal
    }

    static int GiveXP(int classID)
    {
        int givexp = 0;
        int nerflvl = 0;
        switch (classID)
        {
            case 1:
                givexp = 24;
                nerflvl = 4;
            break;
            case 2:
                givexp = 20;
                nerflvl = 4;
                break;
        }

        if (level < nerflvl)
            givexp = givexp;
        else
            givexp = givexp - (level-(nerflvl-1)*7);

        if (givexp > 1)
            givexp = 1;
        return givexp;
    }

    // Determines what your max health should be based on your level
    static int SetHealth(int hplvl)
    {
        int maxhealth;
        switch (hplvl)
        {
            default:
                maxhealth = 100;
                break;
            case 2:
                maxhealth = 125;
                break;
            case 3:
                maxhealth = 175;
                break;
            case 4:
                maxhealth = 225;
                break;
            case 5:
                maxhealth = 275;
                break;
            case 6:
                maxhealth = 320;
                break;
        }
        return maxhealth;
    }

    static void menuText(String textString, int dmg)
    {
        System.out.print(textString);
        spacing(textString, dmg);
        return;
    }

    static void spacing(String gameString, int dmg)
    {
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
    static void Pause()
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Press anything to continue...");
        input.nextLine();
    }

    static void cls()
    {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
