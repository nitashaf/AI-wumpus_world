import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Visual front-end for the Wumpus World first-order logic project.
 * Launch: java Driver   or   java WumpusWorldGUI
 */
public class WumpusWorldGUI extends JFrame {

    private static final String[] CAVE_NAMES = {
        "05x05-1", "05x05-2", "10x10-1", "10x10-2",
        "15x15-1", "15x15-2", "20x20-1", "20x20-2", "25x25-1", "25x25-2"
    };

    private final JComboBox<String> caveCombo = new JComboBox<>(CAVE_NAMES);
    private final JCheckBox showSecretsBox = new JCheckBox("Show hidden dangers (debug)", false);
    private final JButton kbButton = new JButton("Run KB Agent");
    private final JButton reactiveButton = new JButton("Run Reactive Agent");
    private final JLabel statusLabel = new JLabel("Select a cave and run an agent.");
    private final JTextArea logArea = new JTextArea(6, 40);
    private final CavePanel cavePanel = new CavePanel();

    private Driver.SolveResult lastResult;
    private List<int[]> path;
    private int pathIndex;
    private Timer animTimer;
    private boolean[][] visitedOverlay;

    public WumpusWorldGUI() {
        super("Wumpus World — Explorer GUI");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Cave:"));
        top.add(caveCombo);
        top.add(showSecretsBox);
        top.add(kbButton);
        top.add(reactiveButton);
        add(top, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel center = new JPanel(new BorderLayout());
        center.add(new JScrollPane(cavePanel), BorderLayout.CENTER);
        center.add(new JScrollPane(logArea), BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        kbButton.addActionListener(e -> runSolver(true));
        reactiveButton.addActionListener(e -> runSolver(false));
        showSecretsBox.addActionListener(e -> cavePanel.repaint());

        setSize(820, 720);
        setLocationRelativeTo(null);
    }

    private void runSolver(boolean knowledgeBase) {
        stopAnimation();
        kbButton.setEnabled(false);
        reactiveButton.setEnabled(false);
        String cave = (String) caveCombo.getSelectedItem();
        appendLog("Running " + (knowledgeBase ? "KB" : "reactive") + " agent on " + cave + "…\n");

        SwingWorker<Driver.SolveResult, Void> worker = new SwingWorker<>() {
            @Override
            protected Driver.SolveResult doInBackground() {
                return knowledgeBase
                    ? Driver.runKnowledgeSolver(cave)
                    : Driver.runReactiveSolver(cave);
            }

            @Override
            protected void done() {
                try {
                    lastResult = get();
                    visitedOverlay = lastResult.visited;
                    cavePanel.setWorld(lastResult.cave, visitedOverlay, showSecretsBox.isSelected());
                    path = lastResult.agent.movementHistory;
                    pathIndex = 0;
                    String outcome = lastResult.solved
                        ? (lastResult.agent.hasGold ? "Gold collected!" : "Solved.")
                        : (lastResult.agent.alive ? "Search ended without gold." : "Agent died.");
                    appendLog(outcome + " Path length: " + path.size() + "\n");
                    statusLabel.setText(outcome);
                    startPathAnimation();
                } catch (Exception ex) {
                    appendLog("Error: " + ex.getMessage() + "\n");
                    statusLabel.setText("Run failed — see log.");
                    JOptionPane.showMessageDialog(WumpusWorldGUI.this,
                        ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    kbButton.setEnabled(true);
                    reactiveButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void startPathAnimation() {
        if (path == null || path.isEmpty()) {
            return;
        }
        animTimer = new Timer(350, e -> {
            if (pathIndex >= path.size()) {
                stopAnimation();
                cavePanel.setAgentPos(-1, -1);
                if (lastResult != null && lastResult.agent.hasGold) {
                    cavePanel.setAgentPos(path.get(path.size() - 1)[0], path.get(path.size() - 1)[1]);
                }
                cavePanel.repaint();
                return;
            }
            int[] pos = path.get(pathIndex++);
            cavePanel.setAgentPos(pos[0], pos[1]);
            statusLabel.setText(String.format("Animating path %d / %d — cell (%d, %d)",
                pathIndex, path.size(), pos[0], pos[1]));
            cavePanel.repaint();
        });
        animTimer.start();
    }

    private void stopAnimation() {
        if (animTimer != null) {
            animTimer.stop();
            animTimer = null;
        }
    }

    private void appendLog(String text) {
        logArea.append(text);
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private static class CavePanel extends JPanel {
        private CaveReader cave;
        private boolean[][] visited;
        private boolean showSecrets;
        private int agentRow = -1;
        private int agentCol = -1;

        void setWorld(CaveReader cave, boolean[][] visited, boolean showSecrets) {
            this.cave = cave;
            this.visited = visited;
            this.showSecrets = showSecrets;
            agentRow = -1;
            agentCol = -1;
            revalidate();
            repaint();
        }

        void setAgentPos(int row, int col) {
            agentRow = row;
            agentCol = col;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (cave == null) {
                g.drawString("Run an agent to load a cave.", 20, 30);
                return;
            }
            int size = cave.getCaveSize();
            int cell = Math.max(18, Math.min(getWidth(), getHeight()) / (size + 2));

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    Room room = cave.getRoom(r, c);
                    boolean explored = visited != null && visited[r][c];
                    g.setColor(explored ? new Color(248, 250, 252) : new Color(203, 213, 225));
                    g.fillRect(c * cell, (size - 1 - r) * cell, cell, cell);

                    if (showSecrets || explored) {
                        if (room.hasWall()) {
                            g.setColor(new Color(15, 23, 42));
                            g.fillRect(c * cell, (size - 1 - r) * cell, cell, cell);
                        } else if (showSecrets && room.hasPit()) {
                            g.setColor(new Color(55, 48, 163));
                            g.fillOval(c * cell + 4, (size - 1 - r) * cell + 4, cell - 8, cell - 8);
                        } else if (showSecrets && room.hasWumpus()) {
                            g.setColor(new Color(185, 28, 28));
                            g.fillRect(c * cell + 3, (size - 1 - r) * cell + 3, cell - 6, cell - 6);
                        } else if (room.hasGold() && (showSecrets || explored)) {
                            g.setColor(new Color(234, 179, 8));
                            g.fillOval(c * cell + 6, (size - 1 - r) * cell + 6, cell - 12, cell - 12);
                        }
                    }

                    if (agentRow == r && agentCol == c) {
                        g.setColor(new Color(37, 99, 235));
                        g.fillOval(c * cell + 5, (size - 1 - r) * cell + 5, cell - 10, cell - 10);
                    }
                    g.setColor(new Color(100, 116, 139));
                    g.drawRect(c * cell, (size - 1 - r) * cell, cell, cell);
                }
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (cave == null) {
                return new Dimension(320, 320);
            }
            int size = cave.getCaveSize();
            int cell = Math.max(22, 520 / size);
            return new Dimension(size * cell + 20, size * cell + 20);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WumpusWorldGUI().setVisible(true));
    }
}
