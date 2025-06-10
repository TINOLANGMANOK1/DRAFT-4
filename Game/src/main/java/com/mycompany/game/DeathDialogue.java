package com.mycompany.game;

import java.util.Random;

public class DeathDialogue {
    private static final Random rand = new Random();
    private static String playerName = "You";
    private static boolean revealed = false;

    public static void setPlayerName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            playerName = name;
        }
    }

    // Death greets the player after they die, offering a twisted "opportunity"
    public static void onFirstDeath() {
        System.out.println();
        storyPauseLong();
        sayMystery("*A cold silence. Then, a voice echoes in the void...*");
        storyPauseLong();
        sayMystery("\"So this is how it ends, " + playerName + "? Giving up so soon?\"");
        sayPlayer("\"Where... am I? Who's there?\"");
        storyPauseLong();
        sayMystery("\"Most think death is the end. But for you... I have something special in mind.\"");
        sayPlayer("\"I... I didn't expect to wake up again.\"");
        storyPauseLong();
        sayMystery("\"Tell me, what would you give for another chance?\"");
        sayPlayer("\"Anything. Please...\"");
        storyPauseLong();
        sayMystery("*The darkness ripples. You feel the gaze linger - curious, almost playful.*");
        storyPauseLong();
        sayMystery("\"Play my game. Survive my trials. Conquer my Arena. Win, and you return to the world you left behind. Lose, and you belong to me, forever.\"");
        sayPlayer("\"A game? What kind of game?\"");
        storyPauseLong();
        sayMystery("\"Don't disappoint me, " + playerName + ". I do so love a good story.\"");
        storyPauseLong();
    }

    // Reveal Death's name after some progress (e.g., first awakening)
    private static void revealNameIfNeeded() {
        if (!revealed) {
            System.out.println();
            storyPause();
            System.out.println("  ???: *A pause. Then, the shadowy presence finally speaks with a hint of amusement.*");
            System.out.println("  ???: \"You may call me... Death. Or whatever suits you. But names hardly matter here.\"");
            revealed = true;
            storyPause();
        }
    }

    // Player awakens in the game world
    public static void onFirstAwakening() {
        revealNameIfNeeded();
        System.out.println();
        storyPauseLong();
        say("*You gasp awake. Light. Air. The sensation of a new body.*");
        sayPlayer("\"This... this isn't home. Where am I?\"");
        storyPauseLong();
        say("\"Welcome back, " + playerName + ". This world is your stage. Every choice you make, I'll be watching.\"");
        sayPlayer("\"So I really have to fight my way out?\"");
        storyPauseLong();
        say("\"Struggle, grow stronger, and remember - the only way home is through my Arena. Everything else is just rehearsal.\"");
        sayPlayer("\"If that's what it takes... I'll do it.\"");
        storyPauseLong();
    }

    // When the player enters a dungeon for training/leveling
    public static void onDungeonTrainingStart(String dungeon) {
        System.out.println();
        storyPause();
        String[] lines = {
            "\"Into " + dungeon + "? Seeking strength, or running from the inevitable?\"",
            "\"Every monster you slay is one step closer to my Arena. I wonder, will you be ready?\""
        };
        say(lines[rand.nextInt(lines.length)]);
        sayPlayer("\"I have to get stronger. I won't lose here.\"");
        storyPause();
    }

    // When the player clears a dungeon
    public static void onTrainingWin(String dungeon, String rank) {
        System.out.println();
        storyPause();
        String[] msgs = {
            "\"Victory in " + dungeon + " (" + rank + ")... Not bad. But the Arena will not be so forgiving.\"",
            "\"You survived, " + playerName + ". For now. The true test waits, and I can hardly wait to see it.\""
        };
        say(msgs[rand.nextInt(msgs.length)]);
        sayPlayer("\"I won't let it end here. I'm coming for your Arena.\"");
        storyPause();
    }

    // On leveling up, Death taunts and prods the player
    public static void onLevelUp(int level) {
        System.out.println();
        storyPause();
        if (level == 2) {
            say("\"Already adapting? Maybe you do want to live after all.\"");
            sayPlayer("\"I refuse to lose again.\"");
        } else if (level % 10 == 0) {
            say("\"Ten levels... impressive. But don't let it go to your head. My Arena is where legends are made, or broken.\"");
            sayPlayer("\"Bring it on. I'll survive your Arena.\"");
        } else if (level == 50) {
            say("\"Halfway to something. But is it redemption, or just another tragedy?\"");
            sayPlayer("\"This time, I'll choose a different ending.\"");
        } else if (level == 100) {
            say("\"You're strong. But are you ready to stake everything in my Arena, " + playerName + "?\"");
            sayPlayer("\"I'm ready. Ready to live.\"");
        } else {
            String[] msgs = {
                "\"Every level, every victory, I see your hope flicker. Will it survive the Arena?\"",
                "\"Keep pushing. I want to see what you're really made of.\""
            };
            say(msgs[rand.nextInt(msgs.length)]);
            sayPlayer("\"I'll show you. Just watch me.\"");
        }
        storyPause();
    }

    // Entering the Arena: Death becomes both judge and audience
    public static void onArenaEntry() {
        System.out.println();
        storyPause();
        say("\"At last, the final act. My Arena awaits, " + playerName + ". All your training, all your pain, lead here.\"");
        sayPlayer("\"This is it... the last stage.\"");
        storyPauseLong();
        say("\"Entertain me. Show me that mortal will to live. Or accept oblivion with grace.\"");
        sayPlayer("\"I won't lose. Not now.\"");
        storyPauseLong();
    }

    // If the player wins the Arena
    public static void onArenaWin() {
        System.out.println();
        storyPause();
        say("*The Arena falls silent. Even Death seems... moved?*");
        sayPlayer("*Panting, bloodied, but alive* \"I did it. I really did it...\"");
        storyPauseLong();
        say("\"You did it, " + playerName + ". Against fate, against despair, you chose to live. Remarkable.\"");
        sayPlayer("\"I'm not the same person who gave up. Not anymore.\"");
        storyPauseLong();
        say("\"A deal is a deal. The stage is yours once more. This time, make your life worth the encore.\"");
        storyPauseLong();
    }

    // If the player loses in the Arena
    public static void onArenaLose() {
        System.out.println();
        storyPause();
        say("*The world fades. Death's voice is gentle, almost kind.*");
        sayPlayer("\"I... I guess this is really the end, huh?\"");
        storyPauseLong();
        say("\"You fought well, " + playerName + ". But every story must end. Rest now, in my embrace.\"");
        storyPauseLong();
    }

    public static void onQuit() {
        System.out.println();
        storyPause();
        say("\"Stepping off the stage so soon? I expected a better performance. Farewell, " + playerName + ".\"");
        sayPlayer("\"I'm sorry... I just can't anymore.\"");
        storyPause();
    }

    public static void generic(String msg) {
        System.out.println();
        storyPause();
        say(msg);
        System.out.println();
        storyPause();
    }

    public static void say(String msg) {
        revealNameIfNeeded();
        System.out.println("  DEATH: " + msg);
    }

    public static void sayMystery(String msg) {
        System.out.println("  ???: " + msg);
    }

    public static void sayPlayer(String msg) {
        System.out.println("  " + playerName.toUpperCase() + ": " + msg);
    }

    private static void storyPause() {
        try {
            Thread.sleep(950);
        } catch (InterruptedException ignored) {}
    }
    private static void storyPauseLong() {
        try {
            Thread.sleep(1700);
        } catch (InterruptedException ignored) {}
    }
}