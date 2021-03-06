package circlechat.ui;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Stroke;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;


public class ToTheTray 
{
	
	private static TrayIcon toAdd;
	public void toTheTray(final JFrame theFrame)
	{
		Messages_to_Read = 0;
		final SystemTray theTray = SystemTray.getSystemTray();
		
		
		try {
			PopupMenu pm = new PopupMenu();
			MenuItem mi_quit = new MenuItem("Quit");
			pm.add(mi_quit);
			mi_quit.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					ChatClient.close();
				}
				
			});
			
			toAdd = new TrayIcon(getIcon(0),"Circle Chat",pm);
			toAdd.setImageAutoSize(true);
			toAdd.setImage(getIcon(0));
			theTray.add(toAdd);
			toAdd.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					if (arg0.getButton()==MouseEvent.BUTTON1)
					{
						theFrame.setVisible(true);
						theTray.remove(toAdd);
					}
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		theFrame.setVisible(false);
	}
	
	public static int Messages_to_Read = 0;
	
	public static void notifyMessage()
	{
		
		Messages_to_Read++;
		
		if (toAdd !=null)
			toAdd.setImage(getIcon(Messages_to_Read));
	}
	public static Image getIcon(int count)
	{
		BufferedImage img = new BufferedImage(16,16,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(new Color(0,0,0,0));
		g2d.fillRect(0, 0, 16, 16);
		g2d.setColor(Color.red);
		g2d.setStroke(new BasicStroke(1f));
		g2d.drawArc(1, 1, 14, 14, 30, 300);
		g2d.drawArc(4, 4, 8, 8, 210, 300);
		int height = img.getHeight();
		int width  = img.getWidth();
		g2d.setColor(Color.cyan);
		if (count!=0) g2d.drawString(""+Messages_to_Read, width-g2d.getFontMetrics().stringWidth(""+Messages_to_Read), height-1);
		
		return img;
	}
}
