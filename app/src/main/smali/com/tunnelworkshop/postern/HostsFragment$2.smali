.class Lcom/tunnelworkshop/postern/HostsFragment$2;
.super Ljava/lang/Object;
.source "HostsFragment.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/tunnelworkshop/postern/HostsFragment;->onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/tunnelworkshop/postern/HostsFragment;


# direct methods
.method constructor <init>(Lcom/tunnelworkshop/postern/HostsFragment;)V
    .locals 0

    .prologue
    .line 1
    iput-object p1, p0, Lcom/tunnelworkshop/postern/HostsFragment$2;->this$0:Lcom/tunnelworkshop/postern/HostsFragment;

    .line 71
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 5
    .param p1, "v"    # Landroid/view/View;

    .prologue
    const/16 v3, 0x14

    .line 73
    invoke-static {}, Lcom/tunnelworkshop/postern/HostsManager;->getHostsManager()Lcom/tunnelworkshop/postern/HostsManager;

    move-result-object v0

    .line 76
    .local v0, "hostsMgr":Lcom/tunnelworkshop/postern/HostsManager;
    invoke-virtual {v0}, Lcom/tunnelworkshop/postern/HostsManager;->getHostOffset()I

    move-result v2

    if-lt v2, v3, :cond_0

    .line 77
    invoke-virtual {v0}, Lcom/tunnelworkshop/postern/HostsManager;->getHostOffset()I

    move-result v2

    add-int/lit8 v1, v2, -0x14

    .line 80
    .local v1, "newOffset":I
    :goto_0
    invoke-virtual {v0, v1}, Lcom/tunnelworkshop/postern/HostsManager;->setHostOffset(I)V

    .line 81
    invoke-virtual {v0, v1, v3}, Lcom/tunnelworkshop/postern/HostsManager;->refreshHosts(II)V

    .line 84
    invoke-static {}, Lcom/tunnelworkshop/postern/PageManager;->getPageManager()Lcom/tunnelworkshop/postern/PageManager;

    move-result-object v2

    iget-object v3, p0, Lcom/tunnelworkshop/postern/HostsFragment$2;->this$0:Lcom/tunnelworkshop/postern/HostsFragment;

    invoke-virtual {v3}, Lcom/tunnelworkshop/postern/HostsFragment;->getActivity()Landroid/app/Activity;

    move-result-object v3

    const/16 v4, 0xc

    invoke-virtual {v2, v3, v4}, Lcom/tunnelworkshop/postern/PageManager;->setPage(Landroid/app/Activity;I)V

    .line 85
    return-void

    .line 79
    .end local v1    # "newOffset":I
    :cond_0
    const/4 v1, 0x0

    .restart local v1    # "newOffset":I
    goto :goto_0
.end method
