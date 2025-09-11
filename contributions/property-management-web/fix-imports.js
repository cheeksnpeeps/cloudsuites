const fs = require('fs');
const path = require('path');

function fixImportsInFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  const fixed = content.replace(/@[0-9]+\.[0-9]+\.[0-9]+/g, '');
  fs.writeFileSync(filePath, fixed, 'utf8');
  console.log(`Fixed: ${filePath}`);
}

function fixAllFiles(dir) {
  const files = fs.readdirSync(dir);
  
  for (const file of files) {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);
    
    if (stat.isDirectory()) {
      fixAllFiles(filePath);
    } else if (file.endsWith('.tsx') || file.endsWith('.ts')) {
      fixImportsInFile(filePath);
    }
  }
}

const uiDir = '/Users/loc/Documents/GitHub/cloudsuites/contributions/property-management-web/src/components/ui';
fixAllFiles(uiDir);
console.log('Done fixing imports!');
