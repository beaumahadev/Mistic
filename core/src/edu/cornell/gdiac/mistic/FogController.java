package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.WorldController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;


public class FogController {
	PolygonSpriteBatch batch;
	Texture bg;
	String vertexShader;
	String fragmentShader;
	ShaderProgram shader;
	float[][] fogBoard;
	float[][] newFogBoard;
	float[] fogBoardCam;
	BoardModel tileBoard;
	float[][] elementBoard;
	float fogReachX;
	float fogReachY;
	float fogReach;
	int fogDelay;
	Array<Vector2> fogOrigins;
	Vector2 gorfPos;

	private final int FOG_DELAY = 3;
	int spreadType;
	float thickness;
	float spreadCount;
	float spreadCountX;
	float spreadCountY;
	private final int NX = 31;
	private final int NY = 31;

	private int WX;
	private int WY;

	private float tileW;
	private float tileH;
	private float cellW;
	private float cellH;
	private float boardLeftOffset;
	private float boardBotOffset;

	int boardTilesPerCamViewX;
	int boardTilesPerCamViewY;

	float[] litLanternsA;

	float fogOriginCamX;
	float fogOriginCamY;

	private final float BOUNDARY1 = 0.9f;
	private final float BOUNDARY2 = 0.8f;
	private final float BOUNDARY3 = 0.7f;
	private final float FOG = 1.0f;
	private final float WALL = 0.1f;
	private final float LANTERN = 0.5f;

	private final float zoom = .59f;
	Vector2 screenDim;
	float canvasScale;
	Vector2 res;
	Vector2 dim;
	Vector2 scale;

	Array<Vector2> newFog;
	Array<Vector2> fogTiles;

	Texture[] perlinTex;

//	OrthographicCamera cam;
	FPSLogger logger = new FPSLogger();
	Texture boundaryFog;

	FloatArray boundaryTiles;
	FloatArray boundaryTilesCam;
	float[] boundaryTilesCamA;
	Array<Vector2> boundaryPos;
	float[][] boundaryFogBoard;

    private final float S_CORNER_LR = 4f;
    private final float W_CORNER_UD = 5f;
    private final float N_CORNER_LR = 6f;
    private final float E_CORNER_UD = 7f;

	private final float S = 8f;
	private final float W = 9f;
	private final float N = 10f;
	private final float E = 11f;

    private final float S_CORNER_L = 12f;
    private final float W_CORNER_U = 13f;
	private final float N_CORNER_R = 14f;
    private final float E_CORNER_D = 15f;

    private final float S_CORNER_R = 16f;
    private final float W_CORNER_D = 17f;
    private final float N_CORNER_L = 18f;
    private final float E_CORNER_U = 19f;

    private final float SW_CORNER_LR = 20f;
    private final float NW_CORNER_LR = 21f;
    private final float NE_CORNER_LR = 22f;
    private final float SE_CORNER_LR = 23f;

    private final float SW_CORNER_L = 24f;
    private final float NW_CORNER_L = 25f;
    private final float NE_CORNER_L = 26f;
	private final float SE_CORNER_L = 27f;

    private final float NS_CORNER_LL = 28f;
    private final float EW_CORNER_DD = 29f;
    private final float SN_CORNER_RR = 30f;
    private final float WE_CORNER_UU = 31f;

    private final float NS_CORNER_LR = 32f;
    private final float EW_CORNER_DU = 33f;

    private final float SW_CORNER_R = 36f;
    private final float NW_CORNER_R = 37f;
    private final float NE_CORNER_R = 38f;
    private final float SE_CORNER_R = 39f;

    private final float NS_CORNER_RL = 40f;
    private final float EW_CORNER_UD = 41f;

	private final float NW = 44f;
    private final float NE = 45f;
	private final float SE = 46f;
	private final float SW = 47f;

    private final float NS = 48f;
	private final float EW = 49f;

    private final float NS_CORNER_L = 52f;
    private final float EW_CORNER_D = 53f;
    private final float SN_CORNER_R = 54f;
    private final float WE_CORNER_U = 55f;

    private final float NS_CORNER_R = 56f;
    private final float EW_CORNER_U = 57f;
    private final float SN_CORNER_L = 58f;
    private final float WE_CORNER_D = 59f;

    private final float NS_CORNER_D = 60f;
    private final float EW_CORNER_R = 61f;
    private final float NS_CORNER_U = 62f;
    private final float EW_CORNER_L = 63f;

    private final float NS_CORNER_LRL = 64f;
    private final float EW_CORNER_UDD = 65f;
    private final float SN_CORNER_LRR = 66f;
    private final float WE_CORNER_UDU = 67f;

    private final float NS_CORNER_LRR = 68f;
    private final float EW_CORNER_UDU = 69f;
    private final float SN_CORNER_LRL = 70f;
    private final float WE_CORNER_UDD = 71f;

    private final float NS_CORNER_LRLR = 72f;
    private final float EW_CORNER_UDUD = 73f;

    private final float NSW_CORNER_LR = 76f;
	private final float NEW_CORNER_LR = 77f;
    private final float NSE_CORNER_LR = 78f;
    private final float SEW_CORNER_LR = 79f;

    private final float NSW_CORNER_R = 80f;
	private final float NEW_CORNER_R = 81f;
    private final float NSE_CORNER_R = 82f;
    private final float SEW_CORNER_R = 83f;

    private final float NSW_CORNER_L = 84f;
	private final float NEW_CORNER_L = 85f;
    private final float NSE_CORNER_L = 86f;
    private final float SEW_CORNER_L = 87f;

	private final float NSW = 88f;
    private final float NEW = 89f;
	private final float NSE = 90f;
	private final float SEW = 91f;

    private final float NSEW = 92f;

	Texture nTex;
	Texture eTex;
	Texture sTex;
	Texture wTex;
	Texture neTex;
	Texture seTex;
	Texture swTex;
	Texture nwTex;
	Texture nsTex;
	Texture ewTex;
	Texture newTex;
	Texture nseTex;
	Texture nswTex;
	Texture sewTex;

	Texture boundaryTextures;

	Vector2 texOffset;
	Vector2 coeff;

	private final int[] INERTIAL_POWER = {-1, 1, 1, 1, 1};
	private final int[] NEG_POS = {-1, 0, 1};

	Vector2 familiarPos;

	private final int ANIM_SPAN = 360;
	int animFrame;
	boolean dec;

	int fogCount;
    float gorfRadius;


	public FogController(BoardModel tileBoard, GameCanvas canvas, Rectangle screensize, float canvasScale, Vector2 scale, Texture[] perlinTex) {
	    boundaryTextures = new Texture("mistic/fog/boundaries.png");
//		nTex = new Texture("mistic/fog/n_boundary_2.png");
//		eTex = new Texture("mistic/fog/e_boundary.png");
//		sTex = new Texture("mistic/fog/s_boundary.png");
//		wTex = new Texture("mistic/fog/w_boundary.png");
//		neTex = new Texture("mistic/fog/ne_boundary.png");
//		seTex = new Texture("mistic/fog/se_boundary.png");
//		swTex = new Texture("mistic/fog/sw_boundary.png");
//		nwTex = new Texture("mistic/fog/nw_boundary.png");
//		nsTex = new Texture("mistic/fog/ns_boundary.png");
//		ewTex = new Texture("mistic/fog/ew_boundary.png");
//		newTex = new Texture("mistic/fog/new_boundary.png");
//		nseTex = new Texture("mistic/fog/nse_boundary.png");
//		nswTex = new Texture("mistic/fog/nsw_boundary.png");
//		sewTex = new Texture("mistic/fog/sew_boundary.png");

		this.perlinTex = perlinTex;
		animFrame = 0;
		dec = false;

		screenDim = new Vector2(screensize.getWidth(), screensize.getHeight());
		res = new Vector2(canvas.getWidth(), canvas.getHeight());
		this.scale = scale;

		this.canvasScale = canvasScale;
//		this.tileBoard = tileBoard;
//		zoom = canvas.getZoom();

		boundaryTiles = new FloatArray();
		boundaryTilesCam = new FloatArray();
		boundaryTilesCamA = new float[0];
		boundaryPos = new Array<Vector2>();

		fogOrigins = new Array<Vector2>();

		WX = tileBoard.getWidth();
		WY = tileBoard.getHeight();

		tileW = screenDim.x / (float)WX;
		tileH = screenDim.y / (float)WY;

		boardTilesPerCamViewX = (int)Math.ceil(zoom * WX / canvasScale) + 1;
		boardTilesPerCamViewY = (int)Math.ceil(zoom * WY / canvasScale) + 1;

		cellW = boardTilesPerCamViewX*tileW / (float)NX;
		cellH = boardTilesPerCamViewY*tileH / (float)NY;

		fogBoard = new float[WX][WY];
		fogBoardCam = new float[NX*NY];
		newFogBoard = new float[WX][WY];

		elementBoard = new float[WX][WY];

		for (int i=0; i<WX; i++) {
			for (int j=0; j<WY; j++) {
				if (tileBoard.isWall(i,j)) {
					elementBoard[i][j] = WALL;
				}
//				else if (tileBoard.isLantern(i,j)) {
//					elementBoard[i][j] = LANTERN;
//				}
				else if (tileBoard.isFogSpawn(i,j)) {
					fogOrigins.add(new Vector2(i,j));
				}
			}
		}

		for (Vector2 origin : fogOrigins) {
            int x1 = (int)(origin.x - 1 + WX) % WX;
            int x2 = (int)(origin.x + 1     ) % WX;
            int y1 = (int)(origin.y - 1 + WY) % WY;
            int y2 = (int)(origin.y + 1     ) % WY;

            fogBoard[x1][(int)origin.y] = BOUNDARY1;
            fogBoard[x2][(int)origin.y] = BOUNDARY1;
            fogBoard[(int)origin.x][y1] = BOUNDARY1;
            fogBoard[(int)origin.x][y2] = BOUNDARY1;

			fogBoard[(int)origin.x][(int)origin.y] = FOG;
		}

		litLanternsA = new float[0];

		fogReachX = 0;
		fogReachY = 0;
		fogReach = 0;
		fogDelay = 0;
		spreadType = -1;
		thickness = 1;

		vertexShader = Gdx.files.internal("mistic/shaders/fog.vert.glsl").readString();
		fragmentShader = Gdx.files.internal("mistic/shaders/fog.frag.glsl").readString();
		shader = new ShaderProgram(vertexShader, fragmentShader);

		if (!shader.isCompiled()) {
			System.err.println(shader.getLog());
			System.exit(0);
		}

		if (shader.getLog().length()!=0)
			shader.getLog();
			//System.out.println(shader.getLog());

		shader.pedantic = false;

		dim = new Vector2(NX*cellW, NY*cellH);

		shader.begin();
		shader.setUniformf("dim", dim.x/zoom, dim.y/zoom);		// should be NX*cellW? aka graphics width...?
		shader.setUniformf("res", canvas.getWidth(), canvas.getHeight());
		shader.end();

		texOffset = new Vector2();
		coeff = new Vector2();

		familiarPos = new Vector2();

        fogCount = 0;
        fogTiles = new Array<Vector2>();

        gorfRadius = 0;
//		generatePerlin();
	}

	public void screenResize(int width, int height) {
//		cam.setToOrtho(false, width, height);
//		batch.setProjectionMatrix(cam.combined);
		//bind the shader, then set the uniform, then unbind the shader
		shader.begin();
		shader.setUniformf("res", width, height);
		shader.end();
	}

	public ShaderProgram getShader() {
		return shader;
	}

	public void prepShader(int nFireflies) {
		shader.begin();

//		sewTex.bind(15);
//		shader.setUniformi("u_texture_sew", 15);
//
//		newTex.bind(14);
//		shader.setUniformi("u_texture_new", 14);
//
//		nswTex.bind(13);
//		shader.setUniformi("u_texture_nsw", 13);
//
//		nseTex.bind(12);
//		shader.setUniformi("u_texture_nse", 12);
//
//		ewTex.bind(11);
//		shader.setUniformi("u_texture_ew", 11);
//
//		nsTex.bind(10);
//		shader.setUniformi("u_texture_ns", 10);
//
//		nwTex.bind(9);
//		shader.setUniformi("u_texture_nw", 9);
//
//		swTex.bind(8);
//		shader.setUniformi("u_texture_sw", 8);
//
//		seTex.bind(7);
//		shader.setUniformi("u_texture_se", 7);
//
//		neTex.bind(6);
//		shader.setUniformi("u_texture_ne", 6);
//
//		newTex.bind(5);
//		shader.setUniformi("u_texture_new", 5);
//
//		nsTex.bind(4);
//		shader.setUniformi("u_texture_ns", 4);
//
//		neTex.bind(3);
//		shader.setUniformi("u_texture_ne", 3);
//
//		nTex.bind(2);
//		shader.setUniformi("u_texture_n", 2);

        perlinTex[animFrame].bind(1);
		shader.setUniformi("u_texture_perlin", 1);

		boundaryTextures.bind(0);
		shader.setUniformi("u_texture", 0);

		float gorfRadius = .4f*(1f-(float)Math.exp(-nFireflies/3f));

		shader.setUniform1fv("fogBoard", fogBoardCam, 0, NX*NY);
		shader.setUniform2fv("lanterns", litLanternsA, 0, litLanternsA.length);
		shader.setUniformi("numLanterns", litLanternsA.length/2);
		shader.setUniformf("gorfRadius", gorfRadius);
		shader.setUniformf("offset", boardLeftOffset, boardBotOffset);
		shader.setUniform1fv("boundaryTiles", boundaryTilesCamA, 0, boundaryTilesCamA.length);
		shader.setUniformf("tileDim", tileW/zoom, tileH/zoom);
		shader.setUniformf("texOffset", texOffset.x, texOffset.y);
		shader.setUniformf("familiarPos", familiarPos);
		shader.end();
	}

	public void draw(GameCanvas canvas, TextureRegion fboRegion, Vector2 pos) {
		//System.out.println(canvas.getHeight());
		batch = canvas.getSpriteBatch();
//		batch.setShader(shader);

//		batch.begin();
//		Gdx.gl.glClearColor(0, 0, 0, 0);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
		batch.enableBlending();
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batch.draw(boundaryTextures, pos.x, pos.y, screenDim.x, screenDim.y);

		logger.log();
	}

	public void update(GorfModel gorf, ArrayList<Lantern> lanterns, Familiar familiar, int nFireflies, BoardModel tileBoard, GameCanvas canvas, float dt) {
        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for (BoardModel.Tile t : ta) {
                t.isGorfGlow = false;
                t.isLanternGlow = false;
            }
        }
//		fogOriginCamX = (fogOrigin.x / WX * screenDim.x - (gorf.getX() * scale.x - zoom * res.x / 2.0f)) / (zoom * res.x);
//		fogOriginCamY = (fogOrigin.y / WY * screenDim.y - (gorf.getY() * scale.y - zoom * res.y / 2.0f)) / (zoom * res.y);
		gorfPos = new Vector2(gorf.getX() * scale.x, gorf.getY() * scale.y);		// in pixels


		newFog = new Array<Vector2>();
		if (fogDelay <= 0) {
			updateFog(tileBoard);
			fogDelay = FOG_DELAY;
//			fogReach++;
			thickness++;
//			System.out.println(fogReach);
//			System.out.println(fogBoard[(int)(Math.floor(fogOrigin.y/screenDim.y*NY)+Math.floor(fogOrigin.y/screenDim.x*NX)+fogReach)]);
		}
		else {
			fogDelay--;
		}
//		fogReach+=(1f/FOG_DELAY);
//		fogReachX+=(1f/FOG_DELAY * spreadCountX/spreadCount);
//		fogReachY+=(1f/FOG_DELAY * spreadCountY/spreadCount);

        updateLanterns(lanterns, tileBoard);

        boundaryFogBoard = new float[WX][WY];
		for (int q=0; q<WX; q++) {
			boundaryFogBoard[q] = fogBoard[q].clone();
		}
		updateBoundary(tileBoard);


//		System.out.println(gorfPos.x - boardTilesPerCamViewX * tileW / 2f);
		int startTileX;
		int startTileY;
		if (gorf.isCollidingX()) {
			startTileX = (int) Math.floor((gorfPos.x - res.x * zoom / 2f) / screenDim.x * WX);
		} else {
			startTileX = (int) Math.floor((gorfPos.x + (gorf.getFX() * dt * scale.x) - res.x * zoom / 2f) / screenDim.x * WX);
		}
		if (gorf.isCollidingY()) {
			startTileY = (int) Math.floor((gorfPos.y - res.y * zoom / 2f) / screenDim.y * WY);
		} else {
			startTileY = (int) Math.floor((gorfPos.y + (gorf.getFY() * dt * scale.y) - res.y * zoom / 2f) / screenDim.y * WY);

		}

		updateGorfGlow(nFireflies, gorfPos, tileBoard, canvas);

//		if (startTileX % 2 == 1) {
//			startTileX--;
//		}
//		if (startTileY % 2 == 1) {
//			startTileY--;
//		}

//		fogBoard = newFogBoard;
		fogBoardCam = new float[NX*NY];
		int camTileX = 0;
		for (int i=0; i<boardTilesPerCamViewX; i++) {
			int camTileY = 0;
			for (int j=0; j<boardTilesPerCamViewY; j++) {
				int tileX = (startTileX+i + WX) % WX;
				int tileY = (startTileY+j + WY) % WY;
//				if (startTileX+i > 0 && startTileY+j > 0 && startTileX+i < WX && startTileY+j < WY) {
				if (fogBoardCam[camTileY * NX + camTileX] != 0) {
					fogBoardCam[camTileY * NX + camTileX] = Math.min(fogBoardCam[camTileY * NX + camTileX], boundaryFogBoard[tileX][tileY]);
				} else {
					fogBoardCam[camTileY * NX + camTileX] = boundaryFogBoard[tileX][tileY];
				}

				for (int a=0; a<boundaryPos.size; a++) {
					Vector2 b = boundaryPos.get(a);
					if ((int)b.x == tileX && (int)b.y == tileY) {
						boundaryTilesCam.add(boundaryTiles.get(a));
					}
				}
//				}
//				System.out.println(fogBoard[60][60]);
				camTileY++;
			}
			camTileX++;
		}

		boundaryTilesCamA = new float[boundaryTilesCam.size];
		for (int k=0; k<boundaryTilesCam.size; k++) {
			boundaryTilesCamA[k] = boundaryTilesCam.get(k);
//			System.out.println(boundaryTilesCamA);
		}

//		if (gorf.isColliding()) {
//			System.out.println("COlliding is " + gorf.isColliding());
//		}
//		System.out.println(gorf.getFX());
//		System.out.println("Gorf fatness is " + gorf.getWidth());
//		System.out.println(elementBoard[tileBoard.screenToBoardX(gorfPos.x + gorf.getWidth()/2*scale.x)][tileBoard.screenToBoardY(gorfPos.y)]);
//		System.out.println("This thing is " + (gorf.getFX() > 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x) + 1][tileBoard.screenToBoardY(gorfPos.y)] == WALL ||
//				gorf.getFX() < 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x) - 1][tileBoard.screenToBoardY(gorfPos.y)] == WALL ||
//				gorf.getFY() > 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x)    ][tileBoard.screenToBoardY(gorfPos.y + 1)] == WALL ||
//				gorf.getFY() < 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x)    ][tileBoard.screenToBoardY(gorfPos.y - 1)] == WALL));

//		if(gorf.getFX() > 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x) + (int)(gorf.getWidth() / tileW)][tileBoard.screenToBoardY(gorfPos.y)] == WALL ||
//		   gorf.getFX() < 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x) - (int)(gorf.getWidth() / tileW)][tileBoard.screenToBoardY(gorfPos.y)] == WALL ||
//		   gorf.getFY() > 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x)    ][tileBoard.screenToBoardY(gorfPos.y + (int)(gorf.getHeight() / tileH))] == WALL ||
//		   gorf.getFY() < 0 && elementBoard[tileBoard.screenToBoardX(gorfPos.x)    ][tileBoard.screenToBoardY(gorfPos.y - (int)(gorf.getHeight() / tileH))] == WALL) {
//			boardLeftOffset = ((((gorfPos.x - zoom * res.x / 2.0f) + screenDim.x) % screenDim.x) % cellW) / dim.x;
//			boardBotOffset = ((((gorfPos.y - zoom * res.y / 2.0f) + screenDim.y) % screenDim.y) % cellH) / dim.y;
//		} else {
//			boardLeftOffset = ((((gorfPos.x + (gorf.getFX() / 61f * scale.x) - zoom * res.x / 2.0f) + screenDim.x) % screenDim.x) % cellW) / dim.x;
//			boardBotOffset = ((((gorfPos.y + (gorf.getFY() / 61f * scale.y) - zoom * res.y / 2.0f) + screenDim.y) % screenDim.y) % cellH) / dim.y;
//		}
//		System.out.println(gorf.getFX());
//		System.out.println(1/dt);

		if (gorf.isCollidingX()) {
//			System.out.println("X IS COLLIDING, YES");
			boardLeftOffset = ((((gorfPos.x - zoom * res.x / 2.0f) + screenDim.x) % screenDim.x) % cellW) / dim.x;
		} else {
//			System.out.println("X IT IS NOT");
			boardLeftOffset = ((((gorfPos.x + (gorf.getFX() * dt * scale.x) - zoom * res.x / 2.0f) + screenDim.x) % screenDim.x) % cellW) / dim.x;
		}
		if (gorf.isCollidingY()) {
//			System.out.println("Colliding Y");
			boardBotOffset = ((((gorfPos.y - zoom * res.y / 2.0f) + screenDim.y) % screenDim.y) % cellH) / dim.y;
		} else {
//			System.out.println("NOT Y");
			boardBotOffset = ((((gorfPos.y + (gorf.getFY() * dt * scale.y) - zoom * res.y / 2.0f) + screenDim.y) % screenDim.y) % cellH) / dim.y;
		}
//		if (gorf.getX() != gorf.getLastX() || gorf.getY() != gorf.getLastY()) {
////		if ((gorf.getLastFX() != 0 || gorf.getLastFY() != 0) && !gorf.isColliding()) {
//			System.out.println("TRUSDG");
////			System.out.println(gorf.getLastFX());
//		} else {
//
//		}
//		System.out.println(boardLeftOffset);
//		System.out.println(gorf.getX());

//		System.out.println(tileBoard.isFog((int)(gorfPos.x/tileW), (int)(gorfPos.y/tileH)));

		familiarPos = new Vector2((familiar.getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x), (familiar.getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y));

//		if (dec) {
//		    animFrame--;
//		    if (animFrame == 0) {
//		        dec = false;
//            }
//        } else {
//		    animFrame++;
//		    if (animFrame == ANIM_SPAN-1) {
//		        dec = true;
//            }
//        }

        animFrame = (animFrame + 1) % ANIM_SPAN;
	}

	private void updateFog(BoardModel tileBoard) {
		spreadCount = 0;
		spreadCountX = 0;
		spreadCountY = 0;

		newFogBoard = new float[WX][WY];

		for (int q=0; q<WX; q++) {
			newFogBoard[q] = fogBoard[q].clone();
		}

		for (int i = 0; i < WX; i++) {
			for (int j = 0; j < WY; j++) {
				if (fogBoard[i][j] == BOUNDARY1 || fogBoard[i][j] == BOUNDARY3) {
//					System.out.println(newFogBoard[i][j]);
//					System.out.println(fogBoard[i][j]);
					spreadFog(i,j,tileBoard);
					spreadCount++;
					if (spreadType == 0 || spreadType == 1 || spreadType == 2) {
						spreadCountX++;
					}
					if (spreadType == 0 || spreadType == 1 || spreadType == 3) {
						spreadCountY++;
					}
				}
			}
		}
		fogBoard = newFogBoard;

//		int inertia = INERTIAL_POWER[MathUtils.random(0,4)];
//		if (inertia == -1) {
//			coeff.x = NEG_POS[MathUtils.random(0, 2)];
//			do {
//				coeff.y = NEG_POS[MathUtils.random(0, 2)];
//			} while (coeff.x == 0 && coeff.y == 0);
//		}
//		texOffset.x += coeff.x;
//		texOffset.y += coeff.y;
	}

	private void spreadFog(int x, int y, BoardModel tileBoard) {

		spreadType = MathUtils.random(0,20);

		if (spreadType == 0) {
			newFogBoard[x][y] = FOG;
			tileBoard.setFog(x, y, true);
			newFog.add(new Vector2(x,y));
            fogTiles.add(new Vector2(x,y));
			fogCount++;

			int x1 = (x - 1 + WX) % WX;
			int x2 = (x + 1     ) % WX;
			int y1 = (y - 1 + WY) % WY;
			int y2 = (y + 1     ) % WY;

			if (elementBoard[x][y1] == WALL) {
				newFogBoard[x][y1] = FOG;
				tileBoard.setFog(x, y1, true);
			} else {
				newFogBoard[x][y1] = Math.max(newFogBoard[x][y1], BOUNDARY1 * (1 - elementBoard[x][y1]));
			}
			if (elementBoard[x][y2] == WALL) {
				newFogBoard[x][y2] = FOG;
				tileBoard.setFog(x, y2, true);
			} else {
				newFogBoard[x][y2] = Math.max(newFogBoard[x][y2], BOUNDARY1 * (1 - elementBoard[x][y2]));
			}
			if (elementBoard[x1][y] == WALL) {
				newFogBoard[x1][y] = FOG;
				tileBoard.setFog(x1, y, true);
			} else {
				newFogBoard[x1][y] = Math.max(newFogBoard[x1][y], BOUNDARY1 * (1 - elementBoard[x1][y]));
			}
			if (elementBoard[x2][y] == WALL) {
				newFogBoard[x2][y] = FOG;
				tileBoard.setFog(x2, y, true);
			} else {
				newFogBoard[x2][y] = Math.max(newFogBoard[x2][y], BOUNDARY1 * (1 - elementBoard[x2][y]));
			}
		}
		// This causes fog leakage across walls at diagonals...
//		else if (spreadType == 1){
//			int ii, jj;
//			for (int i=x-1; i<=x+1; i++) {
//
//				ii = (i + WX) % WX;
//
//				for (int j=y-1; j<=y+1; j++) {
//
//					jj = (j + WY) % WY;
//
//					if (i == x && j == y) {
//						newFogBoard[i][j] = 1 - elementBoard[i][j];
//						if (newFogBoard[i][j] == 1) {
//							tileBoard.setFog(i,j,true);
//							newFog.add(new Vector2(i,j));
//                            fogTiles.add(new Vector2(i,j));
//						}
//					} else {
//						newFogBoard[ii][jj] = Math.max(newFogBoard[ii][jj], BOUNDARY * (1 - elementBoard[ii][jj]));
//					}
//				}
//			}
//		}

		// Spreading unidirectionally sometimes causes holes to form in the fog, because (actually have no idea why this happens...)
//		else if (spreadType == 2){
//			newFogBoard[x][y] = 1 - elementBoard[x][y];
//
//			int x1 = (x - 1 + WX) % WX;
//			int x2 = (x + 1     ) % WX;
//
//			newFogBoard[x1][y] = Math.max(newFogBoard[x1][y], BOUNDARY * (1 - elementBoard[x1][y]));
//			newFogBoard[x2][y] = Math.max(newFogBoard[x2][y], BOUNDARY * (1 - elementBoard[x2][y]));
//		}
//		else if (spreadType == 3){
//			newFogBoard[x][y] = 1 - elementBoard[x][y];
//
//			int y1 = (y - 1 + WY) % WY;
//			int y2 = (y + 1     ) % WY;
//
//			newFogBoard[x][y1] = Math.max(newFogBoard[x][y1], BOUNDARY * (1 - elementBoard[x][y1]));
//			newFogBoard[x][y2] = Math.max(newFogBoard[x][y2], BOUNDARY * (1 - elementBoard[x][y2]));
//		}
	}

	private void updateBoundary(BoardModel tileBoard) {
		boundaryTiles.clear();
		boundaryPos.clear();

		for (int i=0; i<WX; i++) {
			for (int j = 0; j < WY; j++) {
			    boolean fogAbove = fogBoard[i][(j+1) % WY] == FOG || fogBoard[i][(j+1) % WY] == BOUNDARY3;
			    boolean fogBelow = fogBoard[i][(j-1+WY) % WY] == FOG || fogBoard[i][(j-1+WY) % WY] == BOUNDARY3;
			    boolean fogLeft = fogBoard[(i-1+WX) % WX][j] == FOG || fogBoard[(i-1+WX) % WX][j] == BOUNDARY3;
			    boolean fogRight = fogBoard[(i+1) % WX][j] == FOG || fogBoard[(i+1) % WX][j] == BOUNDARY3;

			    boolean fogUpperLeft = fogBoard[(i-1+WX) % WX][(j+1) % WY] == FOG || fogBoard[(i-1+WX) % WX][(j+1) % WY] == BOUNDARY3;
			    boolean fogUpperRight = fogBoard[(i+1) % WX][(j+1) % WY] == FOG || fogBoard[(i+1) % WX][(j+1) % WY] == BOUNDARY3;
			    boolean fogLowerLeft = fogBoard[(i-1+WX) % WX][(j-1+WY) % WY] == FOG || fogBoard[(i-1+WX) % WX][(j-1+WY) % WY] == BOUNDARY3;
			    boolean fogLowerRight = fogBoard[(i+1) % WX][(j-1+WY) % WY] == FOG || fogBoard[(i+1) % WX][(j-1+WY) % WY] == BOUNDARY3;



				if (fogBoard[i][j] == BOUNDARY1 || fogBoard[i][j] == BOUNDARY2) {
                    if (fogAbove && fogRight && fogBelow && fogLeft) {
                        addBoundary(NSEW, i, j);
                        tileBoard.setFog(i, j, true);
                        newFog.add(new Vector2(i,j));
                        fogTiles.add(new Vector2(i,j));
					}

					else if (fogAbove) {
						if (fogBelow) {
							if (fogLeft) {
                                if (fogUpperRight) {
                                    if (fogLowerRight) {
                                        addBoundary(NSW, i, j);
                                    }
                                    else {
                                        addBoundary(NSW_CORNER_R, i, j);
                                    }
                                }
                                else if (fogLowerRight) {
                                    addBoundary(NSW_CORNER_L, i, j);
                                }
                                else {
                                    addBoundary(NSW_CORNER_LR, i, j);
                                }
							}
							else if (fogRight) {
                                if (fogUpperLeft) {
                                    if (fogLowerLeft) {
                                        addBoundary(NSE, i, j);
                                    }
                                    else {
                                        addBoundary(NSE_CORNER_L, i, j);
                                    }
                                }
                                else if (fogLowerLeft) {
                                    addBoundary(NSE_CORNER_R, i, j);
                                }
                                else {
                                    addBoundary(NSE_CORNER_LR, i, j);
                                }
							}
							else {
                                if (fogLowerLeft) {
                                    if (fogLowerRight) {
                                        if (fogUpperLeft) {
                                            if (fogUpperRight) {
                                                addBoundary(NS, i, j);
                                            } else {
                                                addBoundary(SN_CORNER_R, i, j);
                                            }
                                        } else if (fogUpperRight) {
                                            addBoundary(SN_CORNER_L, i, j);
                                        } else {
                                            addBoundary(NS_CORNER_U, i, j);
                                        }
                                    } else if (fogUpperLeft) {
                                        if (fogUpperRight) {
                                            addBoundary(NS_CORNER_R, i, j);
                                        } else {
                                            addBoundary(SN_CORNER_RR, i, j);
                                        }
                                    } else if (fogUpperRight) {
                                        addBoundary(NS_CORNER_RL, i, j);
                                    } else {
                                        addBoundary(SN_CORNER_LRR, i, j);
                                    }
                                }
                                else if (fogLowerRight) {
                                    if (fogUpperLeft) {
                                        if (fogUpperRight) {
                                            addBoundary(NS_CORNER_L, i, j);
                                        } else {
                                            addBoundary(NS_CORNER_LR, i, j);
                                        }
                                    } else if (fogUpperRight) {
                                        addBoundary(NS_CORNER_LL, i, j);
                                    } else {
                                        addBoundary(SN_CORNER_LRL, i, j);
                                    }
                                }
                                else if (fogUpperLeft) {
                                    if (fogUpperRight) {
                                        addBoundary(NS_CORNER_D, i, j);
                                    } else {
                                        addBoundary(NS_CORNER_LRR, i, j);
                                    }
                                }
                                else if (fogUpperRight) {
                                    addBoundary(NS_CORNER_LRL, i, j);
                                }
                                else {
                                    addBoundary(NS_CORNER_LRLR, i, j);
                                }
							}
						}
						else if (fogLeft) {
                            if (fogRight) {
                                if (fogLowerLeft) {
                                    if (fogLowerRight) {
                                        addBoundary(NEW, i, j);
                                    }
                                    else {
                                        addBoundary(NEW_CORNER_L, i, j);
                                    }
                                }
                                else if (fogLowerRight) {
                                    addBoundary(NEW_CORNER_R, i, j);
                                }
                                else {
                                    addBoundary(NEW_CORNER_LR, i, j);
                                }
                            }
                            else {
                                if (fogUpperRight) {
                                    if (fogLowerLeft) {
                                        addBoundary(NW, i, j);
                                    }
                                    else {
                                        addBoundary(NW_CORNER_R, i, j);
                                    }
                                }
                                else if (fogLowerLeft) {
                                    addBoundary(NW_CORNER_L, i, j);
                                }
                                else {
                                    addBoundary(NW_CORNER_LR, i, j);
                                }
                            }
                        }
                        else if (fogRight) {
						    if (fogUpperLeft) {
                                if (fogLowerRight) {
                                    addBoundary(NE, i, j);
                                }
                                else {
                                    addBoundary(NE_CORNER_L, i, j);
                                }
                            }
                            else if (fogLowerRight) {
						        addBoundary(NE_CORNER_R, i, j);
                            }
                            addBoundary(NE_CORNER_LR, i, j);
                        }
                        else {
						    if (fogUpperLeft) {
						        if (fogUpperRight) {
						            addBoundary(N, i, j);
                                }
                                else {
						            addBoundary(N_CORNER_R, i, j);
                                }
                            }
                            else if (fogUpperRight) {
						        addBoundary(N_CORNER_L, i, j);
                            }
                            else {
                                addBoundary(N_CORNER_LR, i, j);
                            }
                        }
					}

					else if (fogBelow) {
                        if (fogLeft) {
                            if (fogRight) {
                                if (fogUpperLeft) {
                                    if (fogUpperRight) {
                                        addBoundary(SEW, i, j);
                                    }
                                    else {
                                        addBoundary(SEW_CORNER_R, i, j);
                                    }
                                }
                                else if (fogUpperRight) {
                                    addBoundary(SEW_CORNER_L, i, j);
                                }
                                else {
                                    addBoundary(SEW_CORNER_LR, i, j);
                                }
                            }
                            else {
                                if (fogUpperLeft) {
                                    if (fogLowerRight) {
                                        addBoundary(SW, i, j);
                                    }
                                    else {
                                        addBoundary(SW_CORNER_R, i, j);
                                    }
                                }
                                else if (fogLowerRight) {
                                    addBoundary(SW_CORNER_L, i, j);
                                }
                                else {
                                    addBoundary(SW_CORNER_LR, i, j);
                                }
                            }
                        }
                        else if (fogRight) {
                            if (fogUpperRight) {
                                if (fogLowerLeft) {
                                    addBoundary(SE, i, j);
                                }
                                else {
                                    addBoundary(SE_CORNER_L, i, j);
                                }
                            }
                            else if (fogLowerLeft) {
                                addBoundary(SE_CORNER_R, i, j);
                            }
                            else {
                                addBoundary(SE_CORNER_LR, i, j);
                            }
                        }
                        else {
                            if (fogLowerLeft) {
                                if (fogLowerRight) {
                                    addBoundary(S, i, j);
                                }
                                else {
                                    addBoundary(S_CORNER_R, i, j);
                                }
                            }
                            else if (fogLowerRight) {
                                addBoundary(S_CORNER_L, i, j);
                            }
                            else {
                                addBoundary(S_CORNER_LR, i, j);
                            }
                        }
                    }

                    else if (fogLeft) {
					    if (fogRight) {
					        if (fogUpperLeft) {
					            if (fogUpperRight) {
					                if (fogLowerLeft) {
					                    if (fogLowerRight) {
					                        addBoundary(EW, i, j);
                                        }
                                        else {
					                        addBoundary(EW_CORNER_D, i, j);
                                        }
                                    }
                                    else if (fogLowerRight) {
					                    addBoundary(WE_CORNER_D, i, j);
                                    }
                                    else {
					                    addBoundary(EW_CORNER_DD, i, j);
                                    }
                                }
                                else if (fogLowerLeft) {
					                if (fogLowerRight) {
					                    addBoundary(EW_CORNER_U, i, j);
                                    }
                                    else {
					                    addBoundary(EW_CORNER_R, i, j);
                                    }
                                }
                                else if (fogLowerRight) {
                                    addBoundary(EW_CORNER_UD, i, j);
                                }
                                else {
					                addBoundary(EW_CORNER_UDD, i, j);
                                }
                            }
                            else if (fogUpperRight) {
					            if (fogLowerLeft) {
					                if (fogLowerRight) {
					                    addBoundary(WE_CORNER_U, i, j);
                                    }
                                    else {
					                    addBoundary(EW_CORNER_DU, i, j);
                                    }
                                }
                                else if (fogLowerRight) {
                                    addBoundary(EW_CORNER_L, i, j);
                                }
                                else {
					                addBoundary(WE_CORNER_UDD, i, j);
                                }
                            }
                            else if (fogLowerLeft) {
					            if (fogLowerRight) {
                                    addBoundary(WE_CORNER_UU, i, j);
                                }
                                else {
                                    addBoundary(EW_CORNER_UDU, i, j);
                                }
                            }
                            else if (fogLowerRight) {
                                addBoundary(WE_CORNER_UDU, i, j);
                            }
                            else {
					            addBoundary(EW_CORNER_UDUD, i, j);
                            }
                        }
                        else {
					        if (fogUpperLeft) {
                                if (fogLowerLeft) {
                                    addBoundary(W, i, j);
                                }
                                else {
                                    addBoundary(W_CORNER_D, i, j);
                                }
                            }
                            else if (fogLowerLeft) {
                                addBoundary(W_CORNER_U, i, j);
                            }
                            else {
                                addBoundary(W_CORNER_UD, i, j);
                            }
                        }
                    }

                    else if (fogRight) {
                        if (fogUpperRight) {
                            if (fogLowerRight) {
                                addBoundary(E, i, j);
                            }
                            else {
                                addBoundary(E_CORNER_D, i, j);
                            }
                        }
                        else if (fogLowerRight) {
                            addBoundary(E_CORNER_U, i, j);
                        }
                        else {
                            addBoundary(E_CORNER_UD, i, j);
                        }
                    }
				}
			}
		}
	}

	private void addBoundary(float boundary, int i, int j) {
        boundaryTiles.add(boundary);
        boundaryFogBoard[i][j] = boundary;
        boundaryPos.add(new Vector2(i,j));
    }

	private void updateLanterns(ArrayList<Lantern> lanterns, BoardModel tileBoard) {
		Array<Lantern> litLanterns = new Array<Lantern>();
		for (int i=0; i<lanterns.size(); i++) {
			if (lanterns.get(i).lit) {
				litLanterns.add(lanterns.get(i));
			}
		}

		litLanternsA = new float[litLanterns.size*2];
		for (int i=0; i<litLanterns.size; i++) {
			Vector2 lanternPos = new Vector2(tileBoard.screenToBoardX(litLanterns.get(i).getX() * scale.x), tileBoard.screenToBoardY((litLanterns.get(i).getY()+2) * scale.y));

//			int radius = 10;
//
//            int tr = radius;
            int lx, ly;
//            for (int j=-tr; j<=tr; j++) {
//                for (int k = -tr; k <= tr; k++) {
//                    lx = (int)lanternPos.x+k;
//                    ly = (int)lanternPos.y+j;
//                    if (lanternPos.dst(lx, ly) < radius) {
//                        if (radius - lanternPos.dst(lx, ly) < tileW) {
//                            if (elementBoard[lx][ly] != WALL) {
//                                fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY);
////                                fogBoard[lx][ly] = 0f;
//
//                            } else {
//                                fogBoard[lx][ly] = 0f;
//                            }
//                        } else {
//                            fogBoard[lx][ly] = 0f;
//                        }
//                        if (tileBoard.isFog(lx, ly)) {
//                            tileBoard.setFog(lx, ly, false);
//                            fogCount--;
//                        }
//                        tileBoard.setLanternGlow(lx, ly, true);
//                    }
//                }
//            }

			for (int g=-2; g<=2; g++) {
				lx = (int)((lanternPos.x + g + WX) % WX);
				ly = (int)((lanternPos.y - 9 + WY) % WY);
				if (elementBoard[lx][ly] != WALL) {
					fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY3);
				} else {
					fogBoard[lx][ly] = 0f;
				}
				if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
				tileBoard.setLanternGlow(lx, ly, true);
			}

			for (int a=-3; a<=3; a++) {
                lx = (int)((lanternPos.x + a + WX) % WX);
                ly = (int)((lanternPos.y - 8 + WY) % WY);
			    if (a == -3 || a == 3) {
                    if (elementBoard[lx][ly] != WALL) {
                        fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY3);
                    } else {
                        fogBoard[lx][ly] = 0f;
                    }
                } else {
                    fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY2);
                }
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
                tileBoard.setLanternGlow(lx, ly, true);
            }

			for (int j=-7; j<=7; j++) {
				ly = (int) ((lanternPos.y + j + WY) % WY);
				int bound;
                if (j == -7 || j == 7) {
					bound = 2;
				} else if (j == -6 || j == 6 || j == -5 || j == 5) {
					bound = 3;
//				} else if (j == -2 || j == 2) {
//					bound = 2;
//				} else if (j == -5 || j == 5) {
//					bound = 5;
//				} else if (j == -4 || j == 4) {
//					bound = 6;
//				} else if (j == -3 || j == 3) {
//					bound = 6;
				} else {
					bound = 4;
				}

				lx = (int) ((lanternPos.x - bound - 2 + WX) % WX);
				if (elementBoard[lx][ly] != WALL) {
					fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY3);
				} else {
					fogBoard[lx][ly] = 0f;
				}
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
				tileBoard.setLanternGlow(lx, ly, true);

                lx = (int) ((lanternPos.x - bound - 1 + WX) % WX);
                if (elementBoard[lx][ly] != WALL) {
                    fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY2);
                } else {
                    fogBoard[lx][ly] = 0f;
                }
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
                tileBoard.setLanternGlow(lx, ly, true);

				for (int k = -bound; k < bound; k++) {
					lx = (int) ((lanternPos.x + k + WX) % WX);
					fogBoard[lx][ly] = 0f;
                    if (tileBoard.isFog(lx, ly)) {
                        tileBoard.setFog(lx, ly, false);
                        fogCount--;
                    }
					tileBoard.setLanternGlow(lx, ly, true);
				}

				lx = (int) ((lanternPos.x + bound) % WX);
				if (elementBoard[lx][ly] != WALL) {
					fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY2);
				} else {
					fogBoard[lx][ly] = 0f;
				}
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
				tileBoard.setLanternGlow(lx, ly, true);

                lx = (int) ((lanternPos.x + bound + 1) % WX);
                if (elementBoard[lx][ly] != WALL) {
                    fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY3);
                } else {
                    fogBoard[lx][ly] = 0f;
                }
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
                tileBoard.setLanternGlow(lx, ly, true);
			}

            for (int b=-3; b<=3; b++) {
                lx = (int)((lanternPos.x + b + WX) % WX);
                ly = (int)((lanternPos.y + 8 + WY) % WY);
                if (b == -3 || b == 3) {
                    if (elementBoard[lx][ly] != WALL) {
                        fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY3);
                    } else {
                        fogBoard[lx][ly] = 0f;
                    }
                } else {
                    fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY2);
                }
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
                tileBoard.setLanternGlow(lx, ly, true);
            }

			for (int h=-2; h<=2; h++) {
				lx = (int)((lanternPos.x + h + WX) % WX);
				ly = (int)((lanternPos.y + 9) % WY);
				if (elementBoard[lx][ly] != WALL) {
					fogBoard[lx][ly] = Math.min(fogBoard[lx][ly], BOUNDARY3);
				} else {
					fogBoard[lx][ly] = 0f;
				}
                if (tileBoard.isFog(lx, ly)) {
                    tileBoard.setFog(lx, ly, false);
                    fogCount--;
                }
				tileBoard.setLanternGlow(lx, ly, true);
			}

			litLanternsA[i*2] = (litLanterns.get(i).getX() * scale.x + scale.x/2f - (gorfPos.x - zoom * res.x / 2.0f)) / (zoom * res.x);
			litLanternsA[i*2+1] = (litLanterns.get(i).getY() * scale.y + scale.y/2f - (gorfPos.y - zoom * res.y / 2.0f)) / (zoom * res.y);
		}
	}

	public void updateGorfGlow(int nFireflies, Vector2 gorfPos, BoardModel tileBoard, GameCanvas canvas) {
//		float rCap = .45f*canvas.getWidth()*(canvas.getWidth()/canvas.getHeight());
		float rCap = .4f*res.x*zoom/2.0f;
		float radius;
		if (nFireflies == 0) {
		    radius = 0f;
        } else {
            radius = .4f * res.x * zoom / 2.0f + rCap * (1f - (float) Math.exp(-nFireflies / 3.0f));
        }

		int tx = tileBoard.screenToBoardX(gorfPos.x);
		int ty = tileBoard.screenToBoardY(gorfPos.y);

		tileBoard.setGorfGlow(tx, ty, true);
		int tr = (int)(radius/tileW) - 2;
        for (int j=-tr; j<=tr; j++) {
            for (int i = -tr; i <= tr; i++) {
                if (gorfPos.dst(tileBoard.boardtoScreenX(tx+i), tileBoard.boardToScreenY(ty+j)) < radius) {
                    tileBoard.setGorfGlow((tx+i+WX)%WX, (ty+j+WY)%WY, true);
                }
            }
        }
	}

	public Array<Vector2> getNewFog() {
		return newFog;
	}
    public Array<Vector2> getFogTiles() {
        return fogTiles;
    }
    public int getFogCount() { return fogCount; }
}
