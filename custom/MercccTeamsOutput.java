import org.osumercury.badgemaker.*;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;

public class MercccTeamsOutput implements CustomOutput {
	public void save(Renderer r, Progress p,
					 List<Badge> badges, String[] args) {
		PrintWriter w = null;
		try {
			w = new PrintWriter(
					new FileWriter(new File(args[0])));
			w.println("[teams]");
		} catch(IOException ioe) {
			System.err.println("exception: " + ioe);
		}
		for(Badge b : badges) {
			String imgFile = b.number + "-" +
							 b.primaryText + ".png";
			try {
				w.println(b.number + "=" +
						  b.primaryText + "," +
						  b.secondaryText + "," +
						  imgFile);
				ImageIO.write(r.render(b), "png", new File(imgFile));
			} catch(IOException ioe) {
				System.err.println("exception: " + ioe);
			}
		}
		try {
			w.close();
		} catch(Exception ioe) {
			System.err.println("exception: " + ioe);
		}
	}
}
