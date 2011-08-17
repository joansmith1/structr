package org.structr.common.renderer;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.structr.common.AbstractComponent;
import org.structr.common.RenderMode;
import org.structr.common.StructrOutputStream;
import org.structr.core.NodeRenderer;
import org.structr.core.entity.AbstractNode;
import org.structr.help.Container;
import org.structr.help.Paragraph;

/**
 *
 * @author Christian Morgner
 */
public class HtmlComponentRenderer implements NodeRenderer<AbstractComponent> {

	private static final Map<Class, String> tagMap = new LinkedHashMap<Class, String>();
	
	static {
		
		// initialize tag set
		tagMap.put(Container.class, "div");
		tagMap.put(Paragraph.class, "p");
	}
	
	@Override
	public void renderNode(StructrOutputStream output, AbstractComponent currentNode, AbstractNode startNode, String editUrl, Long editNodeId, RenderMode renderMode) {
		
		if(currentNode != null) {
			List<AbstractComponent> components = currentNode.getComponents();
			boolean forceClosingTag = currentNode.getForceClosingTag();
			Map<String, Set> attributes = currentNode.getAttributes();
			String tag = tagMap.get(currentNode.getClass());
			String id = currentNode.getId();

			// notify component of rendering
			currentNode.doBeforeRendering();

			// only render if tag is set!
			if(tag != null)
			{
				output.append("<");
				output.append(tag);

				if(id != null)
				{
					output.append(" id=\"").append(id).append("\"");
				}

				for(Entry<String, Set> attribute : attributes.entrySet())
				{
					output.append(" ");
					output.append(attribute.getKey());
					output.append("=\"");

					for(Iterator it = attribute.getValue().iterator(); it.hasNext();)
					{
						Object attributeValue = it.next();

						output.append(attributeValue);
						if(it.hasNext()) output.append(" ");
					}

					output.append("\"");
				}

				if(components.isEmpty() && !forceClosingTag)
				{
					output.append(" />");

				} else
				{
					output.append(">");
				}
			}

			if(components.isEmpty()) {

				// leaf, render content
				Object[] content = currentNode.getContent();
				if(content != null) {

					for(Object o : content) {
						output.append(o);
					}
				}

			} else {

				// render children
				for(AbstractComponent content : components)
				{
					renderNode(output, content, startNode, editUrl, editNodeId, renderMode);
				}
			}

			if(tag != null && (forceClosingTag || !components.isEmpty()))
			{
				output.append("</");
				output.append(tag);
				output.append(">");
			}
		}
	}

	@Override
	public String getContentType(AbstractComponent currentNode) {
		return "text/html";
	}
	
}
