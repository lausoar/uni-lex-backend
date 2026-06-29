const fs = require('fs');
const path = require('path');

const ROOT = process.argv[2] || process.cwd();

let count = 0;

function processFile(filePath) {
    const buffer = fs.readFileSync(filePath);

    // UTF-8 BOM: EF BB BF
    if (buffer.length >= 3 &&
        buffer[0] === 0xEF &&
        buffer[1] === 0xBB &&
        buffer[2] === 0xBF) {

        const content = buffer.slice(3);
        fs.writeFileSync(filePath, content);

        console.log('✔ Removed BOM:', filePath);
        count++;
    }
}

function walk(dir) {
    const files = fs.readdirSync(dir);

    files.forEach(file => {
        const fullPath = path.join(dir, file);
        const stat = fs.statSync(fullPath);

        if (stat.isDirectory()) {
            // 跳过 node_modules / .git
            if (file === 'node_modules' || file === '.git') return;
            walk(fullPath);
        } else {
            // 只处理代码文件
            if (/\.(js|ts|java|xml|json|yml|yaml|html|css)$/i.test(file)) {
                processFile(fullPath);
            }
        }
    });
}

console.log('🔍 Scanning:', ROOT);
walk(ROOT);
console.log(`\n🎉 Done! Removed BOM from ${count} files.`);