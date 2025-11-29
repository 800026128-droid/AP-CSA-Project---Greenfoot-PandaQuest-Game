/**
 * Star.java
 *
 * Greenfoot Actor that grants the player (Panda) an extra life when collected.
 *
 * Behavior:
 * - When this Star intersects with the Panda actor, it will attempt to increase
 *   the Panda's life count by 1 (using a few common method/field names via reflection).
 * - Plays "star.wav" (if present in the project's sounds) and removes the star
 *   from the world so it cannot be collected again.
 *
 * Notes for integration:
 * - This class expects your player actor class to be named Panda. If your class
 *   is named differently, change the type used in getOneIntersectingObject(...)
 *   and the parameter type in giveExtraLife(...).
 * - The reflection attempts common method names (addLife, gainLife, increaseLife,
 *   oneUp, extraLife) and setters/getters (getLives/setLives, addLives(int)).
 *   If your Panda uses a different API, either add one of those helper methods
 *   to Panda or update giveExtraLife(...) to call the correct method directly.
 *
 * Example straightforward alternative (if your Panda has public int lives):
 *   public class Panda extends Actor {
 *       public int lives = 3;
 *   }
 *
 *   The reflection will also try to read/write a 'lives' field (private or public).
 */

import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.lang.reflect.*;

/** Star actor */
public class Star extends Actor {

    public void act() {
        Panda panda = (Panda) getOneIntersectingObject(Panda.class);
        if (panda != null) {
            boolean success = giveExtraLife(panda);

            // optional sound feedback (ensure "star.wav" exists in project sounds)
            try {
                Greenfoot.playSound("star.wav");
            } catch (Exception e) {
                // ignore if no sound or play fails
            }

            // remove star so it can't be collected again
            World world = getWorld();
            if (world != null) {
                world.removeObject(this);
            }

            // If reflection failed, consider logging to console to help debugging
            if (!success) {
                System.out.println("Star: couldn't automatically increase Panda lives. " +
                                   "Please add a public addLife() or getLives/setLives or public int lives field to Panda.");
            }
        }
    }

    /**
     * Try a variety of common ways to increment the player's life count.
     * Uses reflection so Star doesn't need to know the exact Panda implementation.
     * Returns true if it succeeded, false otherwise.
     */
    private boolean giveExtraLife(Actor pandaActor) {
        Class<?> cls = pandaActor.getClass();

        // 1) Try no-arg methods that imply giving a life
        String[] noArgMethods = { "addLife", "gainLife", "increaseLife", "oneUp", "extraLife" };
        for (String name : noArgMethods) {
            try {
                Method m = cls.getMethod(name);
                m.invoke(pandaActor);
                return true;
            } catch (NoSuchMethodException e) {
                // method not present, continue
            } catch (IllegalAccessException | InvocationTargetException e) {
                // present but couldn't be invoked; continue to other attempts
            }
        }

        // 2) Try methods that accept an int to add lives
        String[] intArgMethods = { "addLives", "gainLives", "increaseLives" };
        for (String name : intArgMethods) {
            try {
                Method m = cls.getMethod(name, int.class);
                m.invoke(pandaActor, 1);
                return true;
            } catch (NoSuchMethodException e) {
                // not present
            } catch (IllegalAccessException | InvocationTargetException e) {
                // invocation problem
            }
        }

        // 3) Try getLives() / setLives(int)
        try {
            Method getLives = cls.getMethod("getLives");
            Object current = getLives.invoke(pandaActor);
            if (current instanceof Number) {
                int curr = ((Number) current).intValue();
                try {
                    Method setLives = cls.getMethod("setLives", int.class);
                    setLives.invoke(pandaActor, curr + 1);
                    return true;
                } catch (NoSuchMethodException e) {
                    // set method not present, try to find a setter with Integer
                    try {
                        Method setLivesWrapper = cls.getMethod("setLives", Integer.class);
                        setLivesWrapper.invoke(pandaActor, Integer.valueOf(curr + 1));
                        return true;
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                        // continue to field-based attempts
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // continue
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // getLives not present or invocation failed, continue
        }

        // 4) Try directly manipulating a 'lives' field (public or private)
        try {
            // try public field first
            Field f = cls.getField("lives");
            if (f.getType() == int.class) {
                int curr = f.getInt(pandaActor);
                f.setInt(pandaActor, curr + 1);
                return true;
            } else if (f.getType() == Integer.class) {
                Integer curr = (Integer) f.get(pandaActor);
                f.set(pandaActor, Integer.valueOf(curr == null ? 1 : curr + 1));
                return true;
            }
        } catch (NoSuchFieldException e) {
            // not public, try declared (private/protected)
            try {
                Field f2 = cls.getDeclaredField("lives");
                f2.setAccessible(true);
                if (f2.getType() == int.class) {
                    int curr = f2.getInt(pandaActor);
                    f2.setInt(pandaActor, curr + 1);
                    return true;
                } else if (f2.getType() == Integer.class) {
                    Integer curr = (Integer) f2.get(pandaActor);
                    f2.set(pandaActor, Integer.valueOf(curr == null ? 1 : curr + 1));
                    return true;
                }
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                // give up on field attempt
            }
        } catch (IllegalAccessException e) {
            // access problem, continue
        }

        // Nothing worked
        return false;
    }
}
