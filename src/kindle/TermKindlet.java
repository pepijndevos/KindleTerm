/*
 * KindleTerminal.java
 * 
 * Copyright (c) 2010 VDP <vdp DOT kindle AT gmail.com>.
 * 
 * This file is part of MidpSSH.
 * 
 * MidpSSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MidpSSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MidpSSH.  If not, see <http ://www.gnu.org/licenses/>.
 */

package kindle;

import app.session.TelnetSession;
import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KImage;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Implementation of the lifecycle of the KindleTerm.
 * 
 * @author VDP <vdp DOT kindle AT gmail.com>
 */
public class TermKindlet extends AbstractKindlet {
    private KindletContext ctx;
    private Container root;
    private KindleTerminal term;
    private TelnetSession session;
    private Logger log;
    private KImage keybLayoutImage;
    private Image keybLayoutImg;

    public void create(KindletContext context) {
        try {
            ctx = context;
            root = ctx.getRootContainer();

            log = Logger.getLogger(TermKindlet.class.getName());
            try {
                log.addAppender(new FileAppender(new PatternLayout("%m%n"),
                        new File(context.getHomeDirectory(), "log.txt").getAbsolutePath()));
            } catch (Throwable t) {
                // well, no logfile then.
            }
            log.setLevel(Level.ALL);

            session = new TelnetSession();

            term = new KindleTerminal(session.getEmulation(),
                    session, ctx);
            term.setFocusable(true);
            term.setFocusTraversalKeysEnabled(false);
            root.add(term, BorderLayout.CENTER);
            
            term.requestFocus();

            root.validate();
            root.setVisible(true);

            session.connect("127.0.0.1", 23);

            log.debug("kindlet's create() finished OK");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void destroy() {
        if (session != null)
            session.disconnect();

        if (term != null)
            term.kill();
    }

    public void start() {
        super.start();
    }

    public void stop() {
    }
}
