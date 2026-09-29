const PDFDocument = require('pdfkit');
const fs = require('fs');
const path = require('path');

const output = path.resolve(__dirname, '..', 'Milestone-4-Demo-Script.pdf');
const doc = new PDFDocument({ size: 'A4', margin: 48, info: { Title: 'SentinelCore SecureOps - Milestone 4 Demo Script' } });
doc.pipe(fs.createWriteStream(output));

const navy = '#102b3a';
const teal = '#167d9a';
const mint = '#12947b';
const amber = '#c77b18';
const ink = '#1b2d36';
const muted = '#657984';
const line = '#d7e5e8';
const soft = '#f2f8f7';

function heading(text) {
  doc.moveDown(0.7).fillColor(navy).font('Helvetica-Bold').fontSize(15).text(text);
  doc.moveTo(48, doc.y + 4).lineTo(547, doc.y + 4).strokeColor(teal).lineWidth(1.5).stroke();
  doc.moveDown(0.45);
}
function paragraph(text, size = 10.5) {
  doc.fillColor(ink).font('Helvetica').fontSize(size).text(text, { lineGap: 3, paragraphGap: 6 });
}
function metric(x, label, value) {
  doc.roundedRect(x, doc.y, 116, 58, 6).fillAndStroke(soft, line);
  doc.fillColor(navy).font('Helvetica-Bold').fontSize(16).text(value, x + 9, doc.y + 9);
  doc.fillColor(muted).font('Helvetica').fontSize(8.5).text(label, x + 9, doc.y + 33);
}

// Header
const headerY = doc.y;
doc.roundedRect(48, headerY, 499, 92, 8).fill(navy);
doc.fillColor('#ffffff').font('Helvetica-Bold').fontSize(24).text('SentinelCore SecureOps', 66, headerY + 22);
doc.font('Helvetica').fontSize(12).text('Milestone 4 | Audit & Compliance Demo Script', 66, headerY + 53);
doc.fontSize(9).fillColor('#bcebe4').text('Governance | RBAC | Audit Evidence | Compliance Reporting', 66, headerY + 72);
doc.y = headerY + 108;

heading('Live Demo Snapshot');
const metricY = doc.y;
metric(48, 'ISO 27001', '94%');
metric(176, 'SOC 2', '98%');
metric(304, 'PCI DSS review', '88%');
metric(432, 'RBAC control', 'ACTIVE');
doc.y = metricY + 72;
doc.roundedRect(48, doc.y, 499, 34, 5).fillAndStroke('#eef8f6', '#b9dfd8');
doc.fillColor(ink).font('Helvetica-Bold').fontSize(9.5).text('Demo login: ', 60, doc.y + 10, { continued: true });
doc.font('Helvetica').text('admin / admin123    Primary screen: http://localhost:5173/compliance');
doc.moveDown(1);

heading('Speaking Script');
doc.roundedRect(48, doc.y, 499, 270, 6).fillAndStroke('#fffaf4', '#eed8b6');
doc.fillColor(ink).font('Helvetica').fontSize(10.2);
doc.text('"Now I will demonstrate Milestone 4: Audit and Compliance.');
doc.text('This milestone provides governance visibility across SentinelCore SecureOps. It verifies regulatory controls, tracks user activity, and supports security review and reporting.');
doc.text('At the top, ISO 27001 is at 94%, SOC 2 is at 98%, and PCI DSS is at 88% and requires review. Each score shows completed checks against the total control count.');
doc.text('Below, the mapped control table connects every result to a framework, auditor, and audit time. Access Control, Cryptography, Firewall Defense, and RBAC controls are passing. The cardholder-data control is flagged for review.');
doc.text('The important capability is traceability. Administrative actions, authentication events, role assignments, and security changes can be reviewed through the Audit Logs workspace.');
doc.text('Role-based access control protects the workflow. Super administrators and security administrators manage the platform, while auditors can review compliance evidence without changing operational data.');
doc.text('Finally, audit records can be filtered and exported for security reviews and reporting. This completes Milestone 4 by connecting compliance verification, access tracking, audit evidence, and reporting in one governance workflow."', { paragraphGap: 5 });
doc.y += 8;

heading('Click Sequence');
const steps = [
  'Open Compliance Center.',
  'Point to the ISO 27001, SOC 2, and PCI DSS score cards.',
  'Point to the passing RBAC control: Role-Based Access Controls.',
  'Point to the PCI DSS warning to show that review items are identified.',
  'Open Audit Logs from the sidebar.',
  'Explain that actions are recorded, filtered, inspected, and exportable.',
  'Return to Compliance and close with the line below.'
];
steps.forEach((step, index) => paragraph(`${index + 1}. ${step}`, 10));

heading('Closing Line');
doc.roundedRect(48, doc.y, 499, 64, 6).fillAndStroke('#eef8f6', '#b9dfd8');
doc.fillColor(navy).font('Helvetica-Bold').fontSize(10.5).text('"SentinelCore SecureOps does not only report compliance scores; it provides the control evidence, access governance, audit trail, and reporting workflow needed to validate them."', 62, doc.y + 15, { width: 470, lineGap: 3 });

doc.end();
console.log(output);
