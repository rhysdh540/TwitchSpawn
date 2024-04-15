package net.programmer.igoodie.twitchspawn;


import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.programmer.igoodie.twitchspawn.tslanguage.parser.TSLSyntaxErrors;

import java.util.LinkedList;
import java.util.List;

public class TwitchSpawnLoadingErrors extends Exception {

	private final List<Exception> exceptions;

	public TwitchSpawnLoadingErrors() {
		this.exceptions = new ObjectArrayList<>();
	}

	public void addException(Exception exception) {
		if(exception instanceof TSLSyntaxErrors syntax)
			this.exceptions.addAll(syntax.getErrors());
		else
			this.exceptions.add(exception);
	}

	public boolean isEmpty() {
		return exceptions.isEmpty();
	}


	/**
	 * Returns the list of exceptions
	 *
	 * @return The exception list.
	 */
	public List<Exception> getExceptions() {
		return this.exceptions;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String delimiter = "";
		for(Exception exception : exceptions) {
			sb.append(delimiter);
			sb.append(exception.getMessage());
			delimiter = "\n";
		}
		return sb.toString();
	}

}
