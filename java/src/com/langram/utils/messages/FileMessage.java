package com.langram.utils.messages;

import java.io.File;

public class FileMessage extends Message
{
    private File content;

    public FileMessage(File f)
    {
        super(MessageType.FILE_MESSAGE);
        this.content = f;
    }


}
