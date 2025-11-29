import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.lang.reflect.*;

/**
 * Star - behaves like Bamboo (static pickup) but grants +1 life to the Panda
 * when collected. Drop this into your project's classes folder alongside
 * Bamboo.java.
 *
 * The code tries several common ways to increment the Panda's lives:
 *  - call a no-arg method like addLife()
 *  - call an int-arg method like addLives(1)
 *  - use getLives()/setLives(int)
 *  - modify a public or private 'lives' field
 *
 * If your Panda class exposes a clear API (e.g. addLife()), the Star will call it.
 * If not, the reflection covers common cases. If you prefer a simpler direct
 * call, tell me the Panda method/field name and I will update this to call it
 * directly (no reflection).
 */
public class Star extends Actor
{
    public Star()
    {
        // Try to set a star image automatically if present in the images folder.
        try {
            GreenfootImage img = new GreenfootImage("star.png");
            if (img != null) {
                setImage(img);
            }
        } catch (Exception e) {
            // ignore: image might not exist
        }
    }

    public void act()
    {
        Panda panda = (Panda) getOneIntersectingObject(Panda.class);
        if (panda != null) {
            boolean success = giveExtraLife(panda);

            // Play collect sound if provided
            try { Greenfoot.playSound("star.wav"); } catch (Exception e) { /* ignore */ }

            // Remove the star so it can't be collected again
            World w = getWorld();
            if (w != null) {
                w.removeObject(this);
            }

            if (!success) {
                System.out.println("Star: couldn't automatically increase Panda lives. " +
                                   "Add a public addLife() or addLives(int) or a public 'lives' field to Panda, " +
                                   "or tell me the method/field name and I'll update Star.java accordingly.");
            }
        }
    }

    /**
     * Try common methods/fields to increment the Panda's lives.
     * Returns true if an attempt succeeded.
     */
    private boolean giveExtraLife(Actor pandaActor) {
        Class<?> cls = pandaActor.getClass();

        // 1) Try common no-arg methods like addLife()
        String[] noArgMethods = { "addLife", "gainLife", "increaseLife", "oneUp", "giveLife", "collectStar" };
        for (String name : noArgMethods) {
            try {
                Method m = cls.getMethod(name);
                m.invoke(pandaActor);
                tryUpdateHUD(cls, pandaActor);
                return true;
            } catch (NoSuchMethodException e) {
                // not present
            } catch (IllegalAccessException | InvocationTargetException e) {
                // could not invoke; keep trying others
            }
        }

        // 2) Try int-arg methods like addLives(int)
        String[] intArgMethods = { "addLives", "gainLives", "increaseLives", "changeLives", "modifyLives" };
        for (String name : intArgMethods) {
            try {
                Method m = cls.getMethod(name, int.class);
                m.invoke(pandaActor, 1);
                tryUpdateHUD(cls, pandaActor);
                return true;
            } catch (NoSuchMethodException e) {
                // not present
            } catch (IllegalAccessException | InvocationTargetException e) {
                // continue
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
                    tryUpdateHUD(cls, pandaActor);
                    return true;
                } catch (NoSuchMethodException e) {
                    // try Integer wrapper
                    try {
                        Method setLives2 = cls.getMethod("setLives", Integer.class);
                        setLives2.invoke(pandaActor, Integer.valueOf(curr + 1));
                        tryUpdateHUD(cls, pandaActor);
                        return true;
                    } catch (Exception ex) { /* continue */ }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // continue
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // continue to field attempts
        }

        // 4) Try public 'lives' field
        try {
            Field f = cls.getField("lives");
            if (f.getType() == int.class) {
                int curr = f.getInt(pandaActor);
                f.setInt(pandaActor, curr + 1);
                tryUpdateHUD(cls, pandaActor);
                return true;
            } else if (f.getType() == Integer.class) {
                Integer curr = (Integer) f.get(pandaActor);
                f.set(pandaActor, Integer.valueOf(curr == null ? 1 : curr + 1));
                tryUpdateHUD(cls, pandaActor);
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
                    tryUpdateHUD(cls, pandaActor);
                    return true;
                } else if (f2.getType() == Integer.class) {
                    Integer curr = (Integer) f2.get(pandaActor);
                    f2.set(pandaActor, Integer.valueOf(curr == null ? 1 : curr + 1));
                    tryUpdateHUD(cls, pandaActor);
                    return true;
                }
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                // give up on field attempts
            }
        } catch (IllegalAccessException e) {
            // continue
        }

        // 5) Last resort: try any method containing "life" in the name
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            String mName = method.getName().toLowerCase();
            try {
                if (mName.contains("life") && method.getParameterCount() == 0) {
                    method.invoke(pandaActor);
                    tryUpdateHUD(cls, pandaActor);
                    return true;
                }
                if (mName.contains("life") && method.getParameterCount() == 1
                    && method.getParameterTypes()[0] == int.class) {
                    method.invoke(pandaActor, 1);
                    tryUpdateHUD(cls, pandaActor);
                    return true;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                // ignore and continue
            }
        }

        return false;
    }

    /**
     * Best-effort HUD refresh after lives changed. Tries common methods on Panda
     * (updateLivesUI, refreshLives) and a static HUD class if present.
     */
    private void tryUpdateHUD(Class<?> cls, Actor pandaActor) {
        String[] hudNames = { "updateLivesUI", "refreshLives", "updateHUD", "updateLifeDisplay" };
        for (String name : hudNames) {
            try {
                Method m = cls.getMethod(name);
                m.invoke(pandaActor);
                return;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
        }
        try {
            Class<?> hudMgr = Class.forName("HUD");
            for (String name : hudNames) {
                try {
                    Method m = hudMgr.getMethod(name);
                    m.invoke(null);
                    return;
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    // continue
                }
            }
        } catch (ClassNotFoundException e) {
            // no HUD manager found; ignore
        }
    }
}
