import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;


public class ToTheTray 
{
	public void toTheTray(final JFrame theFrame)
	{
		final SystemTray theTray = SystemTray.getSystemTray();
		Image img = new BufferedImage(50,50,BufferedImage.TYPE_3BYTE_BGR);
		img.getGraphics().setColor(Color.white);
		img.getGraphics().drawRect(0, 0, 50, 50);
		img.getGraphics().setColor(Color.BLACK);
		img.getGraphics().drawString("That's the stuff", 0, 0);
		
		try {
			final TrayIcon toAdd = new TrayIcon(img);
			theTray.add(toAdd);
			toAdd.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					theFrame.setVisible(true);
					theTray.remove(toAdd);
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
}
