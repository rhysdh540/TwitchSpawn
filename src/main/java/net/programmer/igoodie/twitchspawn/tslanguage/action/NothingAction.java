package net.programmer.igoodie.twitchspawn.tslanguage.action;


import net.programmer.igoodie.twitchspawn.tslanguage.event.EventArguments;
import net.programmer.igoodie.twitchspawn.tslanguage.parser.TSLParser;
import net.programmer.igoodie.twitchspawn.tslanguage.parser.TSLSyntaxError;

import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class NothingAction extends TSLAction {

	public NothingAction(List<String> words) throws TSLSyntaxError {
		this.message = TSLParser.parseMessage(words);
		List<String> actionWords = actionPart(words);

		if(actionWords.size() != 0)
			throw new TSLSyntaxError("Expected 0 arguments, found -> %s", words);
	}

	@Override
	protected void performAction(ServerPlayer player, EventArguments args) {
	}

}
