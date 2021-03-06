package dev.latvian.kubejs.event;

import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.script.ScriptType;

/**
 * @author LatvianModder
 */
public class EventJS
{
	private boolean cancelled = false;

	public boolean canCancel()
	{
		return false;
	}

	public final void cancel()
	{
		cancelled = true;
	}

	@Ignore
	public final boolean isCancelled()
	{
		return cancelled;
	}

	protected void afterPosted(boolean result)
	{
	}

	@Ignore
	public final boolean post(ScriptType t, String id)
	{
		if (t != ScriptType.STARTUP && post(ScriptType.STARTUP, id) && canCancel())
		{
			return true;
		}

		EventsJS e = t.manager.get().events;
		boolean b = e.postToHandlers(id, e.handlers(id), this);
		afterPosted(b);
		return b;
	}

	@Ignore
	public final boolean post(ScriptType t, String id, String sub)
	{
		String id1 = id + '.' + sub;

		if (t != ScriptType.STARTUP)
		{
			EventsJS e = ScriptType.STARTUP.manager.get().events;
			if ((e.postToHandlers(id1, e.handlers(id1), this) || e.postToHandlers(id, e.handlers(id), this)) && canCancel())
			{
				afterPosted(true);
				return true;
			}
		}

		EventsJS e = t.manager.get().events;
		boolean b = e.postToHandlers(id1, e.handlers(id1), this) || e.postToHandlers(id, e.handlers(id), this);
		afterPosted(b);
		return b;
	}
}