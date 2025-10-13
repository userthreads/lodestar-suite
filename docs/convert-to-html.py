#!/usr/bin/env python3
"""
Simple script to convert Markdown documentation to HTML with basic styling.
This makes the documentation more accessible for non-technical users.
"""

import os
import re
from pathlib import Path

def markdown_to_html(markdown_content):
    """Convert Markdown content to HTML with basic styling."""
    
    # Convert headers
    content = re.sub(r'^# (.*)$', r'<h1>\1</h1>', markdown_content, flags=re.MULTILINE)
    content = re.sub(r'^## (.*)$', r'<h2>\1</h2>', content, flags=re.MULTILINE)
    content = re.sub(r'^### (.*)$', r'<h3>\1</h3>', content, flags=re.MULTILINE)
    content = re.sub(r'^#### (.*)$', r'<h4>\1</h4>', content, flags=re.MULTILINE)
    
    # Convert bold text
    content = re.sub(r'\*\*(.*?)\*\*', r'<strong>\1</strong>', content)
    
    # Convert italic text
    content = re.sub(r'\*(.*?)\*', r'<em>\1</em>', content)
    
    # Convert inline code
    content = re.sub(r'`(.*?)`', r'<code>\1</code>', content)
    
    # Convert code blocks
    content = re.sub(r'```(\w+)?\n(.*?)\n```', r'<pre><code>\2</code></pre>', content, flags=re.DOTALL)
    
    # Convert links
    content = re.sub(r'\[([^\]]+)\]\(([^)]+)\)', r'<a href="\2">\1</a>', content)
    
    # Convert lists
    content = re.sub(r'^- (.*)$', r'<li>\1</li>', content, flags=re.MULTILINE)
    content = re.sub(r'^(\d+)\. (.*)$', r'<li>\2</li>', content, flags=re.MULTILINE)
    
    # Wrap consecutive list items in ul tags
    content = re.sub(r'(<li>.*?</li>\n?)+', lambda m: '<ul>' + m.group(0) + '</ul>', content, flags=re.DOTALL)
    
    # Convert paragraphs (text not in other tags)
    lines = content.split('\n')
    result = []
    current_paragraph = []
    
    for line in lines:
        line = line.strip()
        if not line:
            if current_paragraph:
                result.append('<p>' + ' '.join(current_paragraph) + '</p>')
                current_paragraph = []
            result.append('')
        elif line.startswith('<') and line.endswith('>'):
            if current_paragraph:
                result.append('<p>' + ' '.join(current_paragraph) + '</p>')
                current_paragraph = []
            result.append(line)
        else:
            current_paragraph.append(line)
    
    if current_paragraph:
        result.append('<p>' + ' '.join(current_paragraph) + '</p>')
    
    return '\n'.join(result)

def create_html_document(title, content, filename):
    """Create a complete HTML document."""
    
    html_template = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>{title} - Lodestar Suite Documentation</title>
    <style>
        body {{
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            line-height: 1.6;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background: #1a1a1a;
            color: #e0e0e0;
        }}
        .header {{
            text-align: center;
            margin-bottom: 40px;
            padding: 20px 0;
            border-bottom: 1px solid #3a3a3a;
        }}
        .header h1 {{
            margin: 0;
            color: #667eea;
        }}
        .nav {{
            margin: 20px 0;
            padding: 15px;
            background: #2a2a2a;
            border-radius: 5px;
            border: 1px solid #3a3a3a;
        }}
        .nav a {{
            color: #667eea;
            text-decoration: none;
            margin-right: 20px;
        }}
        .nav a:hover {{
            color: #8b7cf6;
        }}
        h1, h2, h3, h4 {{
            color: #667eea;
        }}
        h1 {{
            border-bottom: 2px solid #667eea;
            padding-bottom: 10px;
        }}
        h2 {{
            border-bottom: 1px solid #3a3a3a;
            padding-bottom: 5px;
        }}
        code {{
            background: #2a2a2a;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
        }}
        pre {{
            background: #2a2a2a;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            border: 1px solid #3a3a3a;
        }}
        pre code {{
            background: none;
            padding: 0;
        }}
        ul, ol {{
            padding-left: 20px;
        }}
        li {{
            margin: 5px 0;
        }}
        a {{
            color: #667eea;
            text-decoration: none;
        }}
        a:hover {{
            color: #8b7cf6;
            text-decoration: underline;
        }}
        .footer {{
            text-align: center;
            margin-top: 40px;
            padding: 20px;
            border-top: 1px solid #3a3a3a;
            color: #888;
        }}
    </style>
</head>
<body>
    <div class="header">
        <h1>📚 Lodestar Suite Documentation</h1>
        <p>Your complete guide to the comprehensive Minecraft utility suite</p>
    </div>
    
    <div class="nav">
        <a href="index.html">🏠 Home</a>
        <a href="getting-started.html">🚀 Getting Started</a>
        <a href="user-guide.html">📖 User Guide</a>
        <a href="faq.html">❓ FAQ</a>
        <a href="troubleshooting.html">🛠️ Troubleshooting</a>
        <a href="developer-guide.html">👨‍💻 Developer Guide</a>
        <a href="architecture.html">🏗️ Architecture</a>
    </div>
    
    <div class="content">
        {content}
    </div>
    
    <div class="footer">
        <p>
            <strong>Lodestar Suite</strong> - A comprehensive Minecraft utility suite<br>
            <a href="https://github.com/waythread/lodestar-suite">GitHub Repository</a> | 
            <a href="https://github.com/waythread/lodestar-suite/releases">Releases</a>
        </p>
    </div>
</body>
</html>"""
    
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(html_template)

def main():
    """Convert all Markdown files to HTML."""
    
    docs_dir = Path(__file__).parent
    markdown_files = [
        'README.md',
        'getting-started.md',
        'user-guide.md',
        'faq.md',
        'troubleshooting.md',
        'developer-guide.md',
        'architecture.md'
    ]
    
    for md_file in markdown_files:
        md_path = docs_dir / md_file
        if md_path.exists():
            print(f"Converting {md_file}...")
            
            with open(md_path, 'r', encoding='utf-8') as f:
                markdown_content = f.read()
            
            html_content = markdown_to_html(markdown_content)
            html_filename = md_file.replace('.md', '.html')
            html_path = docs_dir / html_filename
            
            # Extract title from first header
            title_match = re.search(r'^# (.*)$', markdown_content, re.MULTILINE)
            title = title_match.group(1) if title_match else md_file.replace('.md', '').replace('-', ' ').title()
            
            create_html_document(title, html_content, html_path)
            print(f"Created {html_filename}")
    
    print("Conversion complete!")

if __name__ == '__main__':
    main()
