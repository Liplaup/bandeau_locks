package bandeau;

import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Classe utilitaire pour représenter la classe-association UML
 */
class ScenarioElement {

    Effect effect;
    int repeats;

    ScenarioElement(Effect e, int r) {
        effect = e;
        repeats = r;
    }
}

/**
 * Un scenario mémorise une liste d'effets, et le nombre de repetitions pour chaque effet
 * Un scenario sait se jouer sur un bandeau.
 */
public class Scenario {

    private final List<ScenarioElement> myElements = new LinkedList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean isPlaying = false;

    /**
     * Ajouter un effect au scenario.
     *
     * @param e       l'effet à ajouter
     * @param repeats le nombre de répétitions pour cet effet
     */
    public void addEffect(Effect e, int repeats) {
        lock.writeLock().lock();
        try {
            if (isPlaying) {
                throw new IllegalStateException("Cannot modify a scenario while it is playing.");
            }
            myElements.add(new ScenarioElement(e, repeats));
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Jouer ce scenario sur un bandeau
     *
     * @param b le bandeau ou s'afficher.
     */
    public void playOn(BandeauVerrouillable b) {
        Thread t = new Thread(() -> {
            if (!b.tryLock()) {
                System.out.println("Bandeau is already in use.");
                return;
            }
            try {
                lock.readLock().lock();
                isPlaying = true;
                for (ScenarioElement element : myElements) {
                    for (int repeats = 0; repeats < element.repeats; repeats++) {
                        element.effect.playOn(b);
                    }
                }
            } finally {
                isPlaying = false;
                lock.readLock().unlock();
                b.unlock();
            }
        });
        t.start();
    }
}
