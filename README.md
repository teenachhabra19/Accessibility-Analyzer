# Accessibility-Analyzer
📊 Web Accessibility Analyzer
A Spring Boot-based web application that allows users to analyze the accessibility of HTML pages by uploading files or submitting URLs. Inspired by tools like Google Lighthouse, this project evaluates the semantic structure and accessibility features of HTML documents using a weighted scoring algorithm.

✨ Features
📤 Upload .html or .htm files to analyze their accessibility.

🌐 Submit a URL to fetch and analyze a live HTML page.

⚖️ Lighthouse-inspired weighted scoring system:

Prioritizes critical issues (e.g., missing <title>, <html lang>, missing labels).

Assigns scores based on issue category weight instead of flat deductions.

📄 Stores uploaded files and their corresponding reports in a database.

🧠 Built-in integration with Spring AI to allow AI-powered suggestions for improving accessibility (WIP).(Working on this)

