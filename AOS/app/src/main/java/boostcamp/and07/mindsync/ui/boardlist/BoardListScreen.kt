package boostcamp.and07.mindsync.ui.boardlist

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boostcamp.and07.mindsync.R
import boostcamp.and07.mindsync.data.model.Board
import boostcamp.and07.mindsync.ui.dialog.AddBoardScreen
import boostcamp.and07.mindsync.ui.dialog.CreateBoardViewModel
import boostcamp.and07.mindsync.ui.theme.MindSyncTheme
import coil.compose.AsyncImage
import java.io.File

@Composable
fun BoardListScreen(
    boardListViewModel: BoardListViewModel,
    createBoardViewModel: CreateBoardViewModel,
    onCheckBoxClicked: (Board) -> Unit,
    refreshBoard: () -> Unit,
    showDialog: (Boolean) -> Unit,
    deleteBoard: () -> Unit,
    createBoard: (File?, String) -> Unit,
    onAcceptClicked: (Uri) -> Unit,
) {
    val uiState by boardListViewModel.boardUiState.collectAsStateWithLifecycle()
    Scaffold(bottomBar = {
        BoardListBottomBar(
            uiState = uiState,
            addBoard = showDialog,
            deleteBoard = deleteBoard,
            refreshBoard = refreshBoard,
        )
    }) { innerPadding ->
        BoxWithConstraints(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
        ) {
            BoardListComponent(
                uiState = uiState,
                onCheckBoxClicked = onCheckBoxClicked,
            )
            if (uiState.isShownDialog) {
                AddBoardScreen(
                    createBoardViewModel = createBoardViewModel,
                    createBoard = createBoard,
                    updateBoardName = { createBoardViewModel.onBoardNameChanged(it, 0, 0, 0) },
                    createImage = onAcceptClicked,
                    closeDialog = showDialog,
                )
            }
        }
    }
}

@Composable
fun BoardListComponent(
    uiState: BoardListUiState = BoardListUiState(),
    onCheckBoxClicked: (Board) -> Unit = {},
) {
    val scrollState = rememberLazyGridState()
    LazyVerticalGrid(
        state = scrollState,
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(20.dp),
        content = {
            items(
                items = uiState.boards,
                itemContent = { BoardRow(board = it, onCheckBoxClicked = onCheckBoxClicked) },
            )
        },
    )
}

@Composable
fun BoardRow(board: Board, onCheckBoxClicked: (Board) -> Unit) {
    Column {
        Row() {
            Checkbox(checked = board.isChecked, onCheckedChange = { onCheckBoxClicked(board) })
            BoardThumbnail(imageUrl = board.imageUrl)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxSize()) {
            BoardDetail(boardName = board.name, boardDate = board.date)
        }
    }
}

@Composable
fun BoardThumbnail(imageUrl: String) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        placeholder = painterResource(id = R.drawable.ic_placeholder),
        error = painterResource(id = R.drawable.ic_placeholder),
        modifier =
        Modifier
            .size(width = 100.dp, height = 100.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun BoardDetail(boardName: String, boardDate: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = boardName,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = boardDate,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun BoardListBottomBar(
    uiState: BoardListUiState,
    addBoard: (Boolean) -> Unit,
    deleteBoard: () -> Unit,
    refreshBoard: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.Start) {
            FloatingActionButton(onClick = refreshBoard) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh_board),
                    contentDescription = null,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            var icon: Int
            val action: () -> Unit
            if (uiState.selectBoards.isNotEmpty()) {
                icon = R.drawable.ic_delete_board
                action = deleteBoard
            } else {
                icon = R.drawable.ic_add_board
                action = { addBoard(true) }
            }
            FloatingActionButton(onClick = action) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun BoardListPreview() {
    MindSyncTheme {
        BoardListComponent()
    }
}
