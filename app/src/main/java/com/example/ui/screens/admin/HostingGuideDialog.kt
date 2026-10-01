package com.example.ui.screens.admin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeonButton
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HostingGuideDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val nodeJsServerSnippet = """
// NEXUS PANEL - Free Tier Auto Key & UPI Webhook Server (Node.js / Express)
const express = require('express');
const crypto = require('crypto');
const app = express();
app.use(express.json());

const API_SECRET = process.env.NEXUS_API_SECRET || "nexus_live_sec_9948291048";

// 1. External API Key Dispatch Endpoint
app.post('/v1/dispatch', (req, res) => {
  const authHeader = req.headers['authorization'];
  if (authHeader !== `Bearer ${'$'}{API_SECRET}`) {
    return res.status(401).json({ error: "Unauthorized access" });
  }

  const { productId, durationDays, game } = req.body;
  const token = crypto.randomBytes(4).toString('hex').toUpperCase();
  const token2 = crypto.randomBytes(2).toString('hex').toUpperCase();
  const generatedKey = `NEXUS-${'$'}{game ? game.substring(0,4).toUpperCase() : 'GAME'}-${'$'}{durationDays}D-${'$'}{token}-${'$'}{token2}`;

  return res.json({
    status: "SUCCESS",
    licenseKey: generatedKey,
    expiresInDays: durationDays,
    issuedAt: new Date().toISOString()
  });
});

// 2. UPI Webhook for Auto UTR Verification (e.g., Decentro / Cashfree / Razorpay)
app.post('/v1/upi-webhook', (req, res) => {
  const { utr, amount, status } = req.body;
  console.log(`[UPI Webhook] Received UTR: ${'$'}{utr}, Amount: ₹${'$'}{amount}, Status: ${'$'}{status}`);
  // Update your database or call Nexus Panel sync API
  res.json({ received: true });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Nexus Panel Backend running on port ${'$'}{PORT}`));
""".trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        modifier = Modifier.fillMaxWidth().heightIn(max = 680.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FREE-TIER HOSTING & DEPLOYMENT",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Follow these step-by-step instructions to host your NEXUS PANEL web endpoints and external key delivery API on 100% FREE cloud platforms:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                // Option 1: Render.com / Railway
                GuideSectionCard(
                    title = "1. Render.com (100% Free Web Service)",
                    badge = "RECOMMENDED",
                    badgeColor = NeonGreen,
                    steps = listOf(
                        "Sign up for free at render.com (no credit card required).",
                        "Create a New Web Service connected to your GitHub repository containing the Node.js snippet below.",
                        "Set Build Command: npm install, and Start Command: node server.js.",
                        "Add Environment Variable: NEXUS_API_SECRET = your secret key.",
                        "Copy your free live URL (e.g. https://nexus-panel-api.onrender.com) and paste into Admin Settings -> External API Endpoint!"
                    )
                )

                // Option 2: Vercel / Supabase
                GuideSectionCard(
                    title = "2. Vercel Serverless Functions",
                    badge = "INSTANT DEPLOY",
                    badgeColor = NeonCyan,
                    steps = listOf(
                        "Install Vercel CLI (npm i -g vercel) or import repo at vercel.com.",
                        "Place endpoint in /api/dispatch.js as an Express or serverless route handler.",
                        "Deploy instantly with 'vercel --prod' for zero maintenance, global low-latency CDN."
                    )
                )

                // Option 3: UPI Auto-Verification
                GuideSectionCard(
                    title = "3. Auto-Approval UPI Integration",
                    badge = "AUTOMATION",
                    badgeColor = NeonAmber,
                    steps = listOf(
                        "Currently, manual UTR verification is active in your Admin Panel -> Deposits.",
                        "To automate deposits 24/7 without manual admin approval, connect a free UPI notification listener (e.g., SMS forwarder webhook or payment gateway webhook).",
                        "When webhook receives payment confirmation with UTR, call the approveDeposit endpoint automatically."
                    )
                )

                // Code snippet
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NODE.JS BACKEND DISPATCH SERVER",
                            color = NeonPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Node.js Server Code", nodeJsServerSnippet))
                                Toast.makeText(context, "Server code copied!", Toast.LENGTH_SHORT).show()
                            },
                            color = NeonPurple.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPY CODE", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = CyberVoid,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = nodeJsServerSnippet,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "GOT IT, CLOSE GUIDE",
                onClick = onDismiss,
                modifier = Modifier.height(42.dp)
            )
        }
    )
}

@Composable
fun GuideSectionCard(
    title: String,
    badge: String,
    badgeColor: Color,
    steps: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            steps.forEachIndexed { idx, step ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                    Text(text = "•", color = badgeColor, fontSize = 12.sp, modifier = Modifier.width(12.dp))
                    Text(text = step, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }
        }
    }
}
