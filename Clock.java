import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.time.LocalTime;
import java.time.ZoneId;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * @author M.Rin,K.yuuki
 */


public final class Clock extends JPanel {
  private Clock() {
    super(new BorderLayout());
    add(new AnalogClock());
    setPreferredSize(new Dimension(623, 628));
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(Clock::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException ignored) {
      Toolkit.getDefaultToolkit().beep();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
      ex.printStackTrace();
      return;
    }
    JFrame frame = new JFrame("アナログ時計");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(new Clock());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class AnalogClock extends JPanel {
  private LocalTime time = LocalTime.now(ZoneId.systemDefault());
  private final Timer timer = new Timer(200, e -> {
    time = LocalTime.now(ZoneId.systemDefault());
    repaint();
  });
  private transient HierarchyListener listener;

  @Override public void updateUI() {
    removeHierarchyListener(listener);
    super.updateUI();
    listener = e -> {
      if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
        if (e.getComponent().isShowing()) {
          timer.start();
        } else {
          timer.stop();
        }
      }
    };
    addHierarchyListener(listener);
  }

  @Override protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Rectangle rect = SwingUtilities.calculateInnerArea(this, null);
    try {
      BufferedImage image = ImageIO.read(new File("SSS.jpg"));
      g2.drawImage(image, 0, 0, this);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    double radius = Math.min(rect.width, rect.height) / 2d - 10d;
    g2.translate(rect.getCenterX(), rect.getCenterY());

    // Drawing the hour markers
   
   
    AffineTransform at = AffineTransform.getRotateInstance(0d);
    g2.setStroke(new BasicStroke(2f));
    g2.setColor(Color.BLACK);
    for (int i = 0; i < 60; i++) {
      if (i % 5 == 0) {
     
      } else {
       
      }
      at.rotate(Math.PI / 30d);
    }

    // Calculate the angle of rotation
    double secondRot = time.getSecond() * Math.PI / 30d;
    double minuteRot = time.getMinute() * Math.PI / 30d + secondRot / 60d;
    double hourRot = time.getHour() * Math.PI / 6d + minuteRot / 12d;

    // Drawing the hour hand
    double hourHandLen = radius / 20d;
    Shape hourHand = new Line2D.Double(0d, 0d, 0d, -hourHandLen);
    g2.setStroke(new BasicStroke(3f));
    g2.setPaint(Color.BLACK);
    g2.draw(AffineTransform.getRotateInstance(hourRot).createTransformedShape(hourHand));

    // Drawing the minute hand
    double minuteHandLen = 5d * radius / 80d;
    Shape minuteHand = new Line2D.Double(0d, 0d, 0d, -minuteHandLen);
    g2.setStroke(new BasicStroke(2f));
    g2.setPaint(Color.BLACK);
    g2.draw(AffineTransform.getRotateInstance(minuteRot).createTransformedShape(minuteHand));

    // Drawing the second hand
    double r = radius / 10d;
    double secondHandLen = radius - 300d;
    Shape secondHand = new Line2D.Double(0d, r, 0d, -secondHandLen);
    g2.setPaint(Color.BLACK);
    g2.setStroke(new BasicStroke(1f));
    g2.draw(AffineTransform.getRotateInstance(secondRot).createTransformedShape(secondHand));
    g2.fill(new Ellipse2D.Double(-r / 6d, -r / 6d, r / 4d, r / 4d));

    g2.dispose();
  }
}