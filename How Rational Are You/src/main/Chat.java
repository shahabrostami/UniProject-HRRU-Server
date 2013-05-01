package main;

import com.esotericsoftware.kryonet.Client;

import conn.Packet.Packet10ChatMessage;

import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ResizableFrame;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;

public class Chat {
	// create the chat frame for the game
	public static class ChatFrame extends ResizableFrame {
		// set up GUI variables
        private final StringBuilder sb;
        private final HTMLTextAreaModel textAreaModel;
        private final TextArea textArea;
        private final EditField editField;
        private final ScrollPane scrollPane;
        private String message;
        private int playerID;
        private Player player;
    	Client client;
        
    	Packet10ChatMessage chatMessage;

        public ChatFrame() {
        	// initiate the chat frame variables for the game
        	client = HRRUClient.conn.getClient();
        	setTitle("Chat");

            this.sb = new StringBuilder();
            this.textAreaModel = new HTMLTextAreaModel();
            this.textArea = new TextArea(textAreaModel);
            this.editField = new EditField();

            editField.addCallback(new EditField.Callback() {
                public void callback(int key) {
                    if(key == Event.KEY_RETURN) {
                        // cycle through 3 different colors/font styles
                        appendRow("default", editField.getText());
                        editField.setText("");
                    }
                }
            });

            scrollPane = new ScrollPane(textArea);
            scrollPane.setFixed(ScrollPane.Fixed.HORIZONTAL);

            DialogLayout l = new DialogLayout();
            // add the groups for the chat position
            l.setTheme("content");
            l.setHorizontalGroup(l.createParallelGroup(scrollPane, editField));
            l.setVerticalGroup(l.createSequentialGroup(scrollPane, editField));

            add(l);
            // return the player IDs
            playerID = HRRUClient.cs.getPlayer();
            if(playerID == 1)
            	player = HRRUClient.cs.getP1();
            else
            	player = HRRUClient.cs.getP2();
        }

        private void appendRow(String font, String text) {
            sb.append("<div style=\"word-wrap: break-word; font-family: ").append(font).append("; \">");
            // not efficient but simple
            chatMessage = new Packet10ChatMessage();
            chatMessage.playerID = playerID;
            sb.append(player.getName() + ": ");
            message = player.getName() + ": ";
            for(int i=0,l=text.length() ; i<l ; i++) {
                char ch = text.charAt(i);
                switch(ch) {
                    case '<': sb.append("&lt;"); break;
                    case '>': sb.append("&gt;"); break;
                    case '&': sb.append("&amp;"); break;
                    case '"': sb.append("&quot;"); break;
                    case ':':
                        if(text.startsWith(":)", i)) {
                            sb.append("<img src=\"smiley\" alt=\":)\"/>");
                            message = message + ":)";
                            i += 1; // skip one less because of i++ in the for loop
                            break;
                        }
                        message = message + "" + ch;
                        sb.append(ch);
                        break;
                    default:
                    {
                    	message = message + "" + ch;
                    	sb.append(ch);
                    }
                }
            }
            sb.append("</div>");
            chatMessage.message = message;
            client.sendTCP(chatMessage);

            boolean isAtEnd = scrollPane.getMaxScrollPosY() >= scrollPane.getScrollPositionY();

            textAreaModel.setHtml(sb.toString());

            if(isAtEnd) {
                scrollPane.validateLayout();
                scrollPane.setScrollPositionY(scrollPane.getMaxScrollPosY());
            }
        }
        
        public void appendRowOther(String font, String text) {
        	boolean isAtEnd = scrollPane.getMaxScrollPosY() >= scrollPane.getScrollPositionY();

        	sb.append("<div style=\"word-wrap: break-word; font-family: ").append(font).append("; \">");
            // not efficient but simple
            for(int i=0,l=text.length() ; i<l ; i++) {
                char ch = text.charAt(i);
                switch(ch) {
                    case '<': sb.append("&lt;"); break;
                    case '>': sb.append("&gt;"); break;
                    case '&': sb.append("&amp;"); break;
                    case '"': sb.append("&quot;"); break;
                    case ':':
                        if(text.startsWith(":)", i)) {
                            sb.append("<img src=\"smiley\" alt=\":)\"/>");
                            i += 1; // skip one less because of i++ in the for loop
                            break;
                        }
                        sb.append(ch);
                        break;
                    default:
                        sb.append(ch);
                }
            }
            sb.append("</div>");

            textAreaModel.setHtml(sb.toString());

            if(isAtEnd) {
                scrollPane.setScrollPositionY(scrollPane.getMaxScrollPosY());
            }
        }
	}
}