package llplayer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class PlayerMain {

	static PlayerMain thisApp;

	private final JFrame frame;

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

	private JButton playButton;
	private JButton pauseButton;
	private JButton stopButton;
	private JButton rewindButton;
	private JButton skipButton;
    
	private String mediaFilePath;
    private String subtitleFilePath;
    
    private static final int subNum = 2;
    private Sami[] subtitle;

    public static void main(String[] args) {
        thisApp = new PlayerMain();
    }
    
    void createMenu() {
    	JMenuBar menuBar = new JMenuBar();
    	JMenu fileMenu = new JMenu("Menu");
    	JMenuItem openMedia = new JMenuItem("Media Open");
    	
    	openMedia.addActionListener(new ActionListener() {
    		JFileChooser chooser;
            public void actionPerformed(ActionEvent e) {
            	chooser = new JFileChooser();
            	FileNameExtensionFilter filter = new FileNameExtensionFilter(
            			"MP4 & AVI Media",
            			"mp4", "avi");
            	chooser.setFileFilter(filter);
            	
            	int ret = chooser.showOpenDialog(null);
            	if(ret != JFileChooser.APPROVE_OPTION) {
            		//������ �������� ���� ���.
            		return;
            	}
            	
            	mediaFilePath = chooser.getSelectedFile().getPath();
                mediaPlayerComponent.mediaPlayer().media().play(mediaFilePath);
                
                //check default subtitle file.
                subtitle[0] = null;
                subtitle[1] = null;
                
                subtitleFilePath = mediaFilePath.substring(0, mediaFilePath.length() - 3).concat("smi");
                try {
					subtitle[0] = new Sami(subtitleFilePath);
        		} catch (IOException error) {
        			//default subtitle file doesn't exist;
        			subtitle[0] = null;
        		}
            }
        });
    	
    	fileMenu.add(openMedia);
    	menuBar.add(fileMenu);
    	frame.setJMenuBar(menuBar);
    }
    
    JPanel createControlsPane() {
        JPanel controlsPane = new JPanel();
        playButton = new JButton("Play");
        controlsPane.add(playButton);
        pauseButton = new JButton("Pause");
        controlsPane.add(pauseButton);
        stopButton = new JButton("Stop");
        controlsPane.add(stopButton);
        rewindButton = new JButton("Rewind");
        controlsPane.add(rewindButton);
        skipButton = new JButton("Skip");
        controlsPane.add(skipButton);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().play();
            }
        });
        
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().pause();
            }
        });
        
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().stop();
            }
        });

        rewindButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().skipTime(-10000);
            }
        });

        skipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mediaPlayerComponent.mediaPlayer().controls().skipTime(10000);
            }
        });
        
        return controlsPane;
    }
    
    JPanel createSubtitlePane(int num) {
    	JPanel subtitlePane = new JPanel();
    	JButton openButton = new JButton("open" + (num + 1));
        subtitlePane.add(new JLabel("script" + (num + 1)));
        JTextField scriptField = new JTextField(100);
        subtitlePane.add(scriptField);
        subtitlePane.add(openButton);
        
        openButton.addActionListener(new ActionListener() {
    		JFileChooser chooser;
            public void actionPerformed(ActionEvent e) {
            	chooser = new JFileChooser();
            	FileNameExtensionFilter filter = new FileNameExtensionFilter(
            			"SMI Subtitle",
            			"smi");
            	chooser.setFileFilter(filter);
            	
            	int ret = chooser.showOpenDialog(null);
            	if(ret != JFileChooser.APPROVE_OPTION) {
            		//������ �������� ���� ���.
            		return;
            	}
            	
            	subtitleFilePath = chooser.getSelectedFile().getPath();
            	try {
					subtitle[num] = new Sami(subtitleFilePath);
				} catch (IOException error) {
        			//subtitle file doesn't exist;
        			subtitle[num] = null;
				}
            }
        });
        
        return subtitlePane;
    }

    public PlayerMain() {
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayerComponent.release();
                System.exit(0);
            }
        });
        
        subtitle = new Sami[2];
        
        //create Menu Bar;
        createMenu();
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        contentPane.add(mediaPlayerComponent, BorderLayout.CENTER);
        
        JPanel southPane = new JPanel();
        southPane.setLayout(new GridLayout(0, 1));
        
        for(int i = 0; i < subNum; i++) {
            JPanel subtitlePane = createSubtitlePane(i);
            southPane.add(subtitlePane);
        }
        
        JPanel controlsPane = createControlsPane();
        southPane.add(controlsPane);
        
        
        contentPane.add(southPane, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.setVisible(true);
    }
}