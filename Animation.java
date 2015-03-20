import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * La classe Animation charge d'un coup toutes les images nécessaires à l'animation et l'affichage sur l'interface graphique.
 * @author Lenczner Paul & Mathieu Vu
 *
 */
public class Animation implements Deplacement {
	
	public static Image [][] loupRien     = new Image [8][6];
	public static Image [][] loupMarche   = new Image [8][8];
	public static Image [][] loupMange    = new Image [8][9];
	public static Image [][] loupMeurt    = new Image [8][11];
	public static Image [][] moutonRien   = new Image [8][9];
	public static Image [][] moutonMarche = new Image [8][8];
	public static Image [][] moutonMeurt  = new Image [8][9];
	public static Image []   terre		  = new Image [5];
	public static Image []   extra		  = new Image [5];
	public static Image []   herbe	  	  = new Image [14];
	public static Image sels;
	
	public void chargeAllImage() throws IOException {

		// SHEEP DIE
		String dir = "/img/sheep/die/back/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/face/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/left/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/right/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/backright/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/faceright/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/faceleft/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/die/backleft/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMeurt[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		
		// SHEEP IDLE
		dir = "/img/sheep/idle/back/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/backleft/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/backright/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/face/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/faceleft/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/faceright/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/left/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/idle/right/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonRien[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}

		// SHEEP WALK
		dir = "/img/sheep/walk/back/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/backleft/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/backright/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/face/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/faceleft/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/faceright/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/left/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/sheep/walk/right/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.moutonMarche[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}

		// WOLF DIE
		dir = "/img/wolf/die/back/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/face/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/left/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/right/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/backright/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/faceleft/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/backleft/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/die/faceright/";
		for (int i = 0 ; i <= 10 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMeurt[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		
		// WOLF IDLE
		dir = "/img/wolf/idle/back/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/backleft/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/backright/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/face/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/faceleft/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/faceright/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/left/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/idle/right/";
		for (int i = 0 ; i <= 5 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupRien[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}

		// WOLF WALK
		dir = "/img/wolf/walk/back/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/backleft/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/backright/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/face/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/faceleft/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/faceright/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/left/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/walk/right/";
		for (int i = 0 ; i <= 7 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMarche[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}

		// WOLF ATTACK
		dir = "/img/wolf/attack/back/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[HAUT][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/backleft/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[HAUT_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/backright/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[HAUT_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/face/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[BAS][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/faceleft/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[BAS_GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/faceright/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[BAS_DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/left/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[GAUCHE][i] = ImageIO.read(getClass().getResource(path));
		}
		dir = "/img/wolf/attack/right/";
		for (int i = 0 ; i <= 8 ; i++){
			String path = dir + Integer.toString(i) + ".png";
			Animation.loupMange[DROITE][i] = ImageIO.read(getClass().getResource(path));
		}
		
		// Herbe, terre et sels minéraux
		for (int i = 0 ; i < Animation.terre.length ; i++)
			Animation.terre[i] = ImageIO.read(getClass().getResource("/img/terre/terre" +(i+2)+ ".png"));
		for (int i = 0 ; i < Animation.extra.length ; i++)
			Animation.extra[i] = ImageIO.read(getClass().getResource("/img/extra/extra" + i + ".png"));
		Animation.sels = ImageIO.read(getClass().getResource("/img/terre/sels2.png"));
		for (int i = 0 ; i < Animation.herbe.length ; i++)
			Animation.herbe[i] = ImageIO.read(getClass().getResource("/img/terre/herbe" + i + ".png"));
	}
}
