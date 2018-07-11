package net.ncguy.img;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class ViewerForm {
    private JButton pathPickerBtn;
    private JTextField pathField;
    private JPanel imgRoot;
    private JPanel root;
    private JTree fileTree;

    static Map<String, BufferedImage> imageMap;

    public ViewerForm() {
        imageMap = new HashMap<>();

        pathPickerBtn.addActionListener(e -> {
            imgRoot.setLayout(new WrapLayout());
            ClearImagePane();
            fileTree.removeAll();
            String text = pathField.getText();
            File f = new File(text);
            if (!f.exists()) {
                System.out.println(f.toString() + " does not exist");
                return;
            }
            if (!f.isDirectory()) {
                System.out.println(f.toString() + " is not a directory");
                return;
            }
            DefaultTreeModel model = (DefaultTreeModel) fileTree.getModel();
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileWrapper(f));
            model.setRoot(root);
            LoadImages(f, root);
        });

        fileTree.addTreeSelectionListener(e -> UpdateTree());
    }

    void UpdateTree() {
        ClearImagePane();
        TreePath newLeadSelectionPath = fileTree.getSelectionPath();
        if(newLeadSelectionPath == null)
            return;
        DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) newLeadSelectionPath.getLastPathComponent();
        if(lastPathComponent == null)
            return;
        FileWrapper wrapper = (FileWrapper) lastPathComponent.getUserObject();
        if(wrapper == null)
            return;
        LoadImages(wrapper.file, null);
        imgRoot.updateUI();
    }

    public static void GetImage(File file, Consumer<BufferedImage> task) {
        String key = file.toString();
        if(imageMap.containsKey(key)) {
            task.accept(imageMap.get(key));
            return;
        }

        ForkJoinPool.commonPool().execute(() -> {
            try {
                final BufferedImage img = ImageIO.read(file);
                SwingUtilities.invokeLater(() -> {
                    imageMap.put(key, img);
                    task.accept(img);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void ClearImagePane() {
        imgRoot.removeAll();
        imgRoot.updateUI();
    }

    public void LoadImages(File file, DefaultMutableTreeNode node) {


        Runnable extraRunnable = null;
        if (file.isFile()) {
            String name = file.getName();
            extraRunnable = () -> {
                LabeledImagePanel pnl = new LabeledImagePanel(file, name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf(".")));
                imgRoot.add(pnl);
                imgRoot.updateUI();
            };
        }

        final DefaultMutableTreeNode thisNode = new DefaultMutableTreeNode(new FileWrapper(file));

        Runnable finalExtraRunnable = extraRunnable;
        SwingUtilities.invokeLater(() -> {
            if(node != null)
                node.add(thisNode);
            if(finalExtraRunnable != null)
                finalExtraRunnable.run();
        });

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null)
                for (File f : files)
                    ForkJoinPool.commonPool().execute(() -> LoadImages(f, thisNode));
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ViewerForm");
        ViewerForm form = new ViewerForm();
        frame.setContentPane(form.root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static class ImagePanel extends JPanel {
        public File file;
        BufferedImage img;

        public ImagePanel(File file) {
            this.file = file;
            int size = 128;
            setSize(size, size);
            GetImage(file, i -> img = i);
        }

        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (img != null)
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static class LabeledImagePanel extends JPanel {
        ImagePanel panel;

        public LabeledImagePanel(File file, String label) {
            setLayout(new BorderLayout());
            this.panel = new ImagePanel(file);
            add(this.panel, BorderLayout.CENTER);
            add(new JLabel(label), BorderLayout.SOUTH);
            int sizeDim = 128;
            Dimension size = new Dimension(sizeDim, sizeDim + 16);
            setSize(size);
            setMinimumSize(size);
            setPreferredSize(size);
            setMaximumSize(size);
            setBorder(BorderFactory.createBevelBorder(1));

            setToolTipText(label);
        }
    }

    public static class FileWrapper {
        public File file;

        public FileWrapper(File file) {
            this.file = file;
        }

        @Override
        public String toString() {
            String name = file.getName();
            return name;
        }
    }

}
