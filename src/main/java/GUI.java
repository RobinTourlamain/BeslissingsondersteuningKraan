import javax.swing.*;

public class GUI {
    JFrame jframe;
    GUI(){
        jframe = new JFrame();

        JButton b=new JButton("click");
        b.setBounds(130,100,100, 40);
        jframe.add(b);
        jframe.setSize(400,500);//400 width and 500 height
        jframe.setLayout(null);//using no layout managers
        jframe.setVisible(true);//making the frame visible
    }


}
