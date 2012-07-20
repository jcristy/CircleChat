package circlechat.general;

public class Settings 
{
	private static boolean run_background = true;
	private static boolean show_time = false;
	public static boolean isRun_background() {
		return run_background;
	}
	public static void setRun_background(boolean run_background) {
		Settings.run_background = run_background;
	}
	public static boolean isShow_time() {
		return show_time;
	}
	public static void setShow_time(boolean show_time) {
		Settings.show_time = show_time;
	}
}
