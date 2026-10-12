package ui.components;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.awt.Container;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/** Runs blocking service work away from the Swing event thread and restores controls afterward. */
public final class SwingTaskRunner {
    private SwingTaskRunner() { }

    public static <T> void run(JComponent root, AbstractButton trigger, String busyLabel,
                               Callable<T> task, Consumer<T> onSuccess,
                               Consumer<Exception> onFailure) {
        Map<Component, Boolean> priorEnabled = new IdentityHashMap<>();
        rememberAndDisable(root, priorEnabled);
        String originalLabel = trigger == null ? null : trigger.getText();
        if (trigger != null) trigger.setText(busyLabel);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override protected void done() {
                restoreEnabled(priorEnabled);
                if (trigger != null) trigger.setText(originalLabel);
                try {
                    onSuccess.accept(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    onFailure.accept(exception);
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    onFailure.accept(cause instanceof Exception causeException
                            ? causeException : new RuntimeException(cause));
                } catch (RuntimeException exception) {
                    onFailure.accept(exception);
                }
            }
        }.execute();
    }

    public static <T> void run(JComponent root, Callable<T> task,
                               Consumer<T> onSuccess, Consumer<Exception> onFailure) {
        run(root, null, null, task, onSuccess, onFailure);
    }

    public static <T> void run(Container root, Callable<T> task,
                               Consumer<T> onSuccess, Consumer<Exception> onFailure) {
        if (root instanceof JComponent component) {
            run(component, task, onSuccess, onFailure);
            return;
        }
        Map<Component, Boolean> priorEnabled = new IdentityHashMap<>();
        rememberAndDisable(root, priorEnabled);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return task.call(); }
            @Override protected void done() {
                restoreEnabled(priorEnabled);
                try { onSuccess.accept(get()); }
                catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    onFailure.accept(exception);
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    onFailure.accept(cause instanceof Exception causeException
                            ? causeException : new RuntimeException(cause));
                } catch (RuntimeException exception) { onFailure.accept(exception); }
            }
        }.execute();
    }

    private static void rememberAndDisable(Component component, Map<Component, Boolean> priorEnabled) {
        priorEnabled.put(component, component.isEnabled());
        component.setEnabled(false);
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) rememberAndDisable(child, priorEnabled);
        }
    }

    private static void restoreEnabled(Map<Component, Boolean> priorEnabled) {
        priorEnabled.forEach(Component::setEnabled);
    }
}
