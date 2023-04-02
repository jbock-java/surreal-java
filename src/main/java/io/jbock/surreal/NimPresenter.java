package io.jbock.surreal;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class NimPresenter {

    private final NimWindow window;
    private final HistoryManager historyManager;

    private boolean explore;

    private Nim state;

    private NimPresenter(NimWindow window, HistoryManager historyManager) {
        this.window = window;
        this.historyManager = historyManager;
    }

    static NimPresenter create() {
        NimWindow window = NimWindow.create();
        window.setText("Let's start!!!");
        HistoryManager historyManager = new HistoryManager(window);
        NimPresenter presenter = new NimPresenter(window, historyManager);
        window.setOnMove(presenter::onMove);
        window.setOnNewGame(presenter::onNewGame);
        window.setOnNumRowsChanged(presenter::onNumRowsChanged);
        window.setOnExploreChanged(presenter::onExploreChanged);
        window.setOnHistoryClick(presenter::onHistoryClick);
        window.setOnComputerMoveButtonClicked(presenter::onComputerMoveButtonClicked);
        return presenter;
    }

    private void onMove(Nim newState) {
        state = newState;
        historyManager.add(state);
        if (!explore) {
            window.setText(onComputerMoveButtonClicked());
        }
    }

    private void onNewGame() {
        window.setText(explore ? "" : "Let's start!!!");
        window.clearHistory();
        state = Nim.random(state.rows());
        historyManager.add(state);
    }

    private void onNumRowsChanged(int rows) {
        if (rows == state.rows()) {
            return;
        }
        window.setText(explore ? "" : "Let's start!!!");
        window.clearHistory();
        state = Nim.random(rows);
        historyManager.add(state);
    }

    private void onExploreChanged(boolean newValue) {
        explore = newValue;
        if (explore) {
            window.setText("");
        }
        window.setComputerMoveEnabled(!explore);
    }

    private void onHistoryClick(Nim nim) {
        window.setText("");
        state = nim;
        window.set(state);
    }

    private String onComputerMoveButtonClicked() {
        if (state.isEmpty()) {
            historyManager.add(state);
            return "You won!!!";
        }
        List<Nim> moves = state.moves();
        if (!moves.isEmpty()) {
            state = moves.get(ThreadLocalRandom.current().nextInt(moves.size()));
            historyManager.add(state);
            if (state.isEmpty()) {
                return "I won!!!";
            } else {
                return "Phew. I am sure you could do better.";
            }
        }
        state = state.randomMove();
        historyManager.add(state);
        return "Wow. It was a good move.";
    }

    void set(Nim nim) {
        state = nim;
        historyManager.add(nim);
    }
}
